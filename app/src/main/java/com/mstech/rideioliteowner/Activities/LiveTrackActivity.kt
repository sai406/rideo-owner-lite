package com.mstech.rideioliteowner.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.MyUtils
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.custom_window_layout.view.*
import okhttp3.RequestBody
import org.json.JSONObject

class LiveTrackActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var builder: LatLngBounds.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_track)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        if (NetworkUtils.isConnected()) {
            allLiveVehicles()
        } else {
            ToastUtils.showShort("No Internet Connection")
        }
    }

    private fun allLiveVehicles() {
        mMap.clear()
        val markersList: MutableList<Marker> =
            ArrayList()
        builder = LatLngBounds.Builder()
        var cu: CameraUpdate
        MyUtils.showProgress(this, true)
        var obj = JSONObject()

        obj.put("OwnerId", SPStaticUtils.getString(SharedKey.OWNER_ID,"0"))
        obj.put("DriverId",0)
        obj.put("VehicleId", 0)
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client.allLiveVehicles(body).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                MyUtils.showProgress(this@LiveTrackActivity, false)
                if (response.isSuccessful) {
                    ToastUtils.showShort("enrtered")
                    var data = response?.body()?.string()
                    try {
                        var array = JSONArray(data)
                        var icon = BitmapDescriptorFactory.fromResource(R.drawable.car)
                        for (x in 0 until array.length()) {
                            var livedata = array.getJSONObject(x)
                            var tripdata = livedata.getJSONObject("liveData")
                            var latitude = tripdata.getDouble("latitude")
                            var longitude = tripdata.getDouble("longitude")
                            var driverId = tripdata.getDouble("driverId")
                            val markerdata =
                                mMap.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            latitude, longitude
                                        )
                                    ).title(tripdata.getString("fullName")).snippet(tripdata.getString("driverId"))
                                        .icon(icon)
                                )
                            markersList.add(markerdata)
//                            val customInfoWindow = CustomInfoWindowGoogleMap(this@LiveTrackActivity)
//                            mMap.setInfoWindowAdapter(customInfoWindow)
//                            lastlatitude = latitude
//                            lastlongitude = longitude
                        }
                        for (m in markersList) {
                            builder!!.include(m.position)
                        }
                        val padding = 50
                        val bounds = builder!!.build()
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                        mMap.animateCamera(cu)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                MyUtils.showProgress(this@LiveTrackActivity, false)
                LogUtils.e(t.message)
            }

        })
        mMap.setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener { marker ->
            SPStaticUtils.put(SharedKey.IMEI, marker.snippet)
            val title = marker.snippet
            startActivity(Intent(this,SingleVehicleTrack::class.java).putExtra("latitude",marker.position.latitude).putExtra("longitude",marker.position.longitude))
            LogUtils.e(title)
        })
    }

    class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter {

        override fun getInfoContents(p0: Marker?): View {

            var mInfoView =
                (context as Activity).layoutInflater.inflate(R.layout.custom_window_layout, null)
            var mInfoWindow: InfoWindowData? = p0?.tag as InfoWindowData?

            mInfoView.vehicle_number.text = mInfoWindow?.vehicleNumber
            mInfoView.date.text = mInfoWindow?.receivedtime
            mInfoView.speed.text = mInfoWindow?.speed
            mInfoView.status.text = mInfoWindow?.status

            return mInfoView
        }

        override fun getInfoWindow(p0: Marker?): View? {
            return null
        }
    }

    data class InfoWindowData(
        val vehicleNumber: String,
        val receivedtime: String,
        val speed: String,
        val status: String
    )
}
