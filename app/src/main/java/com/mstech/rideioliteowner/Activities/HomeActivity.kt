package com.mstech.rideioliteowner.Activities

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPStaticUtils
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    var mytrip: ImageView? = null
    var mytrack: ImageView? = null
    var memories: ImageView? = null
    var social: ImageView? = null
    var doubleBackToExitPressedOnce = false
    lateinit  var manager : LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mytrip = findViewById(R.id.mytrip)
        social = findViewById(R.id.social)
        mytrack = findViewById(R.id.mytrack)
        memories = findViewById(R.id.memories)
logout.setOnClickListener( View.OnClickListener {
    logout("Are You Sure Want to LogOut")
})
        mytrip!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    LiveTrackActivity::class.java
                )
            )
        })
        mytrack!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    TripsActivity::class.java
                )
            )
        })
        memories!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    DriverListActivity::class.java
                )
            )
        })
        social!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,AlertsActivity::class.java))
        })
    }
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    private fun logout(statusMessage: String) {
        val builder =
            AlertDialog.Builder(this@HomeActivity)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage(statusMessage)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                SPStaticUtils.clear()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        val alert = builder.create()
        alert.show()
    }
}