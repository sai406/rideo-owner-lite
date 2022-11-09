package com.mstech.rideioliteowner.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
var driverid:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        try {
            driverid = intent.getStringExtra("driverid")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        geofence.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,GeofenceActivity::class.java).putExtra("driverid",driverid))
        })
    }
}
