package com.mstech.rideioliteowner.Activities

import androidx.appcompat.app.AppCompatActivity

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.webkit.WebView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.commissionsinc.androidsignalr.Hub
import com.commissionsinc.androidsignalr.SignalR
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideioliteowner.R
import com.mstech.rideioliteowner.Utils.LatLngInterpolator
import kotlinx.android.synthetic.main.custom_window_layout.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class SingleVehicleTrack : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var statusTextView: TextView
    private lateinit var webView: WebView
    private lateinit var map: SupportMapFragment
    private lateinit var mapFragment: SupportMapFragment
    var circle: Circle? = null
    var Title = ""
    var vLatLng: LatLng? = null
    var vMarker: Marker? = null
    var lastlatitude = 0.0
    var lastlongitude = 0.0
    var started = false
    private var menu: Menu? = null
    val logTag = "MapFragment"
    lateinit var chatHub: Hub
    lateinit var connection: SignalR
    lateinit var endtrip: FloatingActionButton
    lateinit var map_layout: RelativeLayout
    lateinit var notrip: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_vehicle_track)
        map_layout = findViewById(R.id.map_layout)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        statusTextView = findViewById<TextView>(R.id.statusTextView)
        webView = findViewById<WebView>(R.id.webView)
            if (NetworkUtils.isConnected()) {
                signalR()
            } else {
                ToastUtils.showShort("No Internet Connection")
            }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val latLng = LatLng(-28.000290, 153.430880)
        googleMap!!.uiSettings.isMyLocationButtonEnabled = false
        googleMap!!.uiSettings.isZoomControlsEnabled = true

        // Updates the location and zoom of the MapView
        // Updates the location and zoom of the MapView
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        googleMap!!.moveCamera(cameraUpdate)
        val customInfoWindow = CustomInfoWindowGoogleMap(this)

        mMap.setInfoWindowAdapter(customInfoWindow)
    }

    fun signalR() {
        connection = SignalR(
            this,
            "http://www.launchapps.com.au/signalr",
            SignalR.ConnectionType.HUB,
            webView
        )
        connection.signalRVersion = SignalR.SignalRVersion.v2_2_2

        chatHub = Hub("trackHub")
        chatHub.on("UpdatedVehicleLiveData") { args ->
//            Log.d(logTag, args.toString())
            LogUtils.e(args.toString())
            
            var array = JSONArray(args.toString())
            for (i in 0..array.length() - 1) {
                var obj = array.getJSONObject(i)
                try {
                    var lat = obj.getDouble("Latitude")
                    var lon = obj.getDouble("Longitude")
                    var angle = obj.getDouble("Angle")
                    var date = obj.getString("DataDay")
                    var speed = obj.getString("SpeedKmph")
//                    var ignition = obj.getString("IgnitionStatus")
//                    var status = obj.getString("VehicleEvent")
                    var Imei = obj.getString("DriverId")
                    if(Imei.equals(SPStaticUtils.getString(SharedKey.IMEI, ""))){
                        startCar(lat, lon, angle, date, speed)
                        val info = InfoWindowData(
                            SPStaticUtils.getString(SharedKey.FIRSTNAME, ""),
                            date,
                            speed
                        )
                        vMarker?.tag = info
                    }

                } catch (e: Exception) {
e.printStackTrace()
                }

            }

        }

        connection.addHub(chatHub)

        //SignalR events
        connection.starting = {
            statusTextView.text = getString(R.string.starting)
        }

        connection.reconnecting = {
            statusTextView.text = getString(R.string.reconnecting)
        }

        connection.connected = {
            Log.d(logTag, "Connection ID: ${connection.connectionID}")
            statusTextView.text = getString(R.string.connected)
            started = true
        }

        connection.reconnected = {
            statusTextView.text = getString(R.string.reconnected)
            started = true
        }

        connection.disconnected = {
            statusTextView.text = getString(R.string.disconnected)
            started = false
        }

        connection.connectionSlow = {
            Log.d(logTag, "Connection Slow")
        }

        connection.error = { error ->
            Log.d(logTag, "Error: ${error.toString()}")
        }

        connection.start()

    }

    private fun startCar(
        lat: Double,
        lon: Double,
        angle: Double,
        date: String,
        speed: String
    ) {
        if (lastlatitude != 0.0) {
            val line = mMap.addPolyline(
                PolylineOptions()
                    .add(LatLng(lastlatitude, lastlongitude), LatLng(lat, lon))
                    .width(7f)
                    .color(Color.BLACK)
            )
        }
        lastlatitude = lat
        lastlongitude = lon
        val dLatLng = LatLng(lat, lon)
        var mLatLngInterpolator = LatLngInterpolator.Linear()
        if (vLatLng != null) {
            val vstartPosition = vMarker!!.position
            if (vstartPosition.latitude != dLatLng.latitude && vstartPosition.longitude != dLatLng.longitude) {
                Log.e("vMarker", "vMarker")
                animateMarkerNew(dLatLng, vMarker!!, angle)
            } else {
                Log.e("vMarkerFalse", "vMarker")
                vMarker!!.isVisible = true
                vMarker!!.position = dLatLng
                vMarker!!.rotation = vMarker!!.rotation
            }
            vLatLng = vMarker!!.position
        } else {
            mLatLngInterpolator = LatLngInterpolator.Linear()
            if (dLatLng != null) {
                Log.e("802", "bbbbbb" + dLatLng.longitude)
                vLatLng = dLatLng
                vMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(vLatLng!!)
                        .title(Title)
                        .icon(
                            BitmapDescriptorFactory
                                .fromResource(R.drawable.car)
                        )
                )
                circle = mMap.addCircle(
                    CircleOptions()
                        .center(vLatLng)
                        .radius(2000.0)
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(1.0f)
                        .fillColor(Color.TRANSPARENT)
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vLatLng, 18f))
            }
        }
//vMarker?.title="VehicleNo : "+SPStaticUtils.getString(SharedKey.VEHICLENUMBER)+"\n"+"Received Time "+date+"\n"+"Speed : "+speed+"\n"+"Status : "+status

//        vMarker?.showInfoWindow()
    }


    fun startStopPressed() {
        Log.d(logTag, "startStopPressed")

        if (started) {
            connection.stop()
        } else {
            connection.start()
        }
    }

    private fun animateMarkerNew(destination: LatLng, marker: Marker?, angle: Double) {
        if (marker != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)

            val startRotation = marker.rotation
            val latLngInterpolator = LatLngInterpolatorNew.LinearFixed()

            val valueAnimator = ValueAnimator.ofFloat(0F, 1F)
            valueAnimator.duration =
                3000 // duration 3 second            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition)
                    marker.position = newPosition
                    mMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(17.5f)
                                .build()
                        )
                    )

                    marker.rotation = angle.toFloat()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
            })
            valueAnimator.start()
        }
    }

    interface LatLngInterpolatorNew {
        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

        class LinearFixed : LatLngInterpolatorNew {
            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }


    class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter {

        override fun getInfoContents(p0: Marker?): View {

            var mInfoView =
                (context as Activity).layoutInflater.inflate(R.layout.custom_window_layout, null)
            var mInfoWindow: InfoWindowData? = p0?.tag as InfoWindowData?

            mInfoView.vehicle_number.text = mInfoWindow?.vehicleNumber
            mInfoView.date.text = mInfoWindow?.receivedtime
            mInfoView.speed.text = mInfoWindow?.speed
//            mInfoView.status.text = mInfoWindow?.status

            return mInfoView
        }

        override fun getInfoWindow(p0: Marker?): View? {
            return null
        }
    }

    data class InfoWindowData(
        val vehicleNumber: String,
        val receivedtime: String,
        val speed: String
    )
}


