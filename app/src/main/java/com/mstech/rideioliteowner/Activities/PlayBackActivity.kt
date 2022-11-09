package com.mstech.rideioliteowner.Activities

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mstech.rideiodriverlite.Utils.AppConstants
import com.mstech.rideiodriverlite.Utils.MyUtils.showProgress
import com.mstech.rideioliteowner.R
import com.mstech.rideioliteowner.Utils.LatLngInterpolator
import kotlinx.android.synthetic.main.activity_play_back.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.util.*

class PlayBackActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    var googleMap: GoogleMap? = null
    var latLngList: MutableList<LatLng> = ArrayList()
    var anglelist: MutableList<Float> = ArrayList()
    var speedlist: MutableList<String> = ArrayList()
    var datelist: MutableList<String> = ArrayList()
    var ignitionlist: MutableList<String> = ArrayList()
    private var polylineOptions: PolylineOptions? = null
    private var greyPolyLine: Polyline? = null
    private var handler: Handler? = null
    private var carMarker: Marker? = null
    private var index = 0
    private var next = 0
    private var startPosition: LatLng? = null
    private var endPosition: LatLng? = null
    private var v = 0f
    var polyLineList: List<LatLng>? = null
    private var lat = 0.0
    private var lng = 0.0
    var tripid: String? = null
    var latitude = 23.7877649
    var longitude = 90.4007049
    var playing = true
    var normalmap = true
    private val TAG = "HomeActivity"
    var simpleSeekBar: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_back)
//        setScreenTitle("PlayBack")
//        getBackButton().setOnClickListener(View.OnClickListener {
//            onBackPressed()
//        })
//        getHomeButton().setOnClickListener(View.OnClickListener {
//            startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
//        })
        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottom_sheet)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        var data = intent.extras
        if (data != null) {
            tripid = data.getString("tripid")
        }

        handler = Handler()
        if(NetworkUtils.isConnected()){
            getTripHistory(tripid)
        }else{
            ToastUtils.showShort("No Internet Connection")
        }
        simpleSeekBar = findViewById<View>(R.id.simpleSeekBar) as SeekBar
        var play = findViewById<View>(R.id.play) as ImageButton
        simpleSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                index = progressChangedValue
            }
        })
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
       play.setOnClickListener(View.OnClickListener { v ->
           if (playing) {
               playing = false
               play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp)
               stopRepeatingTask()
           } else {
               playing = true
               play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp)
               startRepeatingTask()
           }
       })
        map_type.setOnClickListener(View.OnClickListener { v ->
           if (normalmap) {
               normalmap = false
               map_type.setBackgroundResource(R.drawable.position)
               googleMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
           } else {
               normalmap = true
               map_type.setBackgroundResource(R.drawable.space)
               googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
           }
       })
    }
    override fun onPause() {
        super.onPause()
        stopRepeatingTask()
    }

    override fun onResume() {
        super.onResume()
            startRepeatingTask()


    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    val staticCarRunnable = object : Runnable {
        override fun run() {
            try {
                if (index < polyLineList!!.size - 1) {
                    index++
                    next = index + 1
                } else {
                    index = -1
                    next = 1
                    play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp)
                    stopRepeatingTask()
                    return
                }

                if (index < polyLineList!!.size - 1) { //                startPosition = polyLineList.get(index);
                    startPosition = carMarker!!.position
                    endPosition = polyLineList!![next]
                }
                simpleSeekBar!!.progress = index


                var valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.duration = 1000
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addUpdateListener { valueAnimator ->
                    v = valueAnimator.animatedFraction
                    lng = v * endPosition!!.longitude + (1 - v) * startPosition!!.longitude
                    lat = v * endPosition!!.latitude + (1 - v) * startPosition!!.latitude
                    val newPos = LatLng(lat, lng)
                    carMarker!!.position = newPos
                    carMarker!!.setAnchor(0.5f, 0.5f)
                    carMarker!!.rotation = anglelist[index]
                    date.text = datelist[index]
                    speed.text = speedlist[index]
//                    if(ignitionlist[index].equals("0")){
//                        ignition.text = "OFF"
//                    }else{
//                        ignition.text = "ON"
//                    }

                    googleMap!!.moveCamera(
                        CameraUpdateFactory
                            .newCameraPosition(
                                CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(16.5f)
                                    .build()
                            )
                    )
                }
                valueAnimator.start()

                handler!!.postDelayed(this, 1500)

            }catch (e:Exception){
                e.printStackTrace()
            }
                }
    }

    fun stopRepeatingTask() {
        if (staticCarRunnable != null) {
            handler!!.removeCallbacks(staticCarRunnable)
        }
    }
    fun startRepeatingTask() {
        play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp)
        if (polyLineList != null) handler!!.postDelayed(staticCarRunnable, 500)
    }

    private fun startCarAnimation(
        latitude: Double,
        longitude: Double
    ) {
        val latLng = LatLng(latitude, longitude)
        if (carMarker != null) {
            carMarker!!.remove()
        }
        carMarker = googleMap!!.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
        )
        index = -1
        next = 1
        if (polyLineList!!.size>=2){
            handler!!.postDelayed(staticCarRunnable, 500)
        }else{
            ToastUtils.showShort("No Trips Found")
        }

    }

    private fun getTripHistory(tripid: String?) {
        try {
            showProgress(this, true)
            val requestQueue = Volley.newRequestQueue(this)
            val params = JSONObject()
            params.put("TripId", tripid)
            val mRequestBody = params.toString()
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                AppConstants.GET_TRIP_HISTORY,
                Response.Listener { response ->
                    Log.d("response", response)
                    showProgress(this, false)
                    try {
                        var objdata = JSONObject(response)
                        vehicle_no.text = objdata.getString("vehicleNumber")
                        max_speed.text = objdata.getString("maxSpeedInKmph")
                        avg_speed.text = objdata.getString("avgSpeedInKmph")
                        total_distance.text = objdata.getString("distanceInKm")
                        start_address.text = objdata.getString("startAddress")+"\n"+ objdata.getString("startDay")
                        end_address.text = objdata.getString("endAddress")+"\n"+ objdata.getString("endDay")
                        val array = (objdata.getJSONArray("tripHistory"))
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            if (obj.getString("latitude").toString() != null && obj.getString("longitude").toString() != null) {
                                val mLatLngInterpolator: LatLngInterpolator =
                                    LatLngInterpolator.Linear()
                                val lati = obj.getDouble("latitude")
                                val longi = obj.getDouble("longitude")
                                val dLatLng = LatLng(lati, longi)
                                latLngList.add(dLatLng)
                            }
                            try {
                                anglelist.add(obj.getString("angle").toFloat())
                                speedlist.add(obj.getString("speedKmph"))
                                datelist.add(obj.getString("dataDay"))
                                ignitionlist.add(obj.getString("ignitionStatus"))
                            } catch (e: Exception) {
                                anglelist.add("0.0".toFloat())
                            }
                        }
                        polyLineList = latLngList
                        simpleSeekBar!!.max = polyLineList!!.size-1
                        googleMap!!.clear()
                        val builder = LatLngBounds.Builder()
                        for (latLng in polyLineList!!) {
                            builder.include(latLng)
                        }
                        val bounds = builder.build()
                        val mCameraUpdate =
                            CameraUpdateFactory.newLatLngBounds(bounds, 2)
                        googleMap!!.animateCamera(mCameraUpdate)
                        polylineOptions = PolylineOptions()
                        polylineOptions!!.color(Color.BLACK)
                        polylineOptions!!.width(5f)
                        polylineOptions!!.startCap(SquareCap())
                        polylineOptions!!.endCap(SquareCap())
                        polylineOptions!!.jointType(JointType.ROUND)
                        polylineOptions!!.addAll(polyLineList)
                        greyPolyLine = googleMap!!.addPolyline(polylineOptions)
                        startCarAnimation(latitude, longitude)
                        //                        staticPolyLine(polyLineList);
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    showProgress(this, false)
                    Log.e("LOG_VOLLEY", error.toString()) }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray? {
                    return try {
                        mRequestBody.toByteArray(
                            charset(
                                "utf-8"
                            )
                        )
                    } catch (uee: UnsupportedEncodingException) {
                        VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            mRequestBody,
                            "utf-8"
                        )
                        return null
                    }
                }
            }
            requestQueue.add(stringRequest)
            stringRequest.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}
