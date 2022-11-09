package com.mstech.rideioliteowner.Activities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mstech.rideioliteowner.R


class ShowAlertsMapActivity : AppCompatActivity(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null
    var latitude : Double = 0.0
    var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_alerts_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        var data = intent.extras
        if (data != null) {
            latitude = data.getDouble("lat")
            longitude = data.getDouble("lon")
        }

    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        val latLng = LatLng(latitude, longitude)
        googleMap!!.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker))
                .anchor(0.0f, 1.0f)
                .position(latLng)
        )
        googleMap!!.uiSettings.isMyLocationButtonEnabled = false
        googleMap!!.uiSettings.isZoomControlsEnabled = true

        // Updates the location and zoom of the MapView
        // Updates the location and zoom of the MapView
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        googleMap!!.moveCamera(cameraUpdate)
    }
}
