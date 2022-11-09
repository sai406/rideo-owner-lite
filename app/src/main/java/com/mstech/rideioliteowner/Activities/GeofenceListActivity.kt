package com.mstech.rideioliteowner.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.MyUtils.showProgress
import com.mstech.rideioliteowner.Adapter.GeofenceListAdapter
import com.mstech.rideioliteowner.Model.GeofenceListResponse
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_geofence_list.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class GeofenceListActivity : AppCompatActivity() {
    var geofencelist: List<GeofenceListResponse> =
        ArrayList<GeofenceListResponse>()
    var driverid = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence_list)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = linearLayoutManager
        try {
            driverid = intent.getIntExtra("driverid",0)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        floatingActionButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,GeofenceActivity::class.java).putExtra("driverid",driverid))
        })
    }

    override fun onResume() {
        super.onResume()
        if(NetworkUtils.isConnected()){
            getGeofenceList()
        }else{
            ToastUtils.showShort("No Internet Connection")
        }
    }
    private fun getGeofenceList() {
        showProgress(this, true)
        var obj = JSONObject()
        obj.put("OwnerId", SPStaticUtils.getString(SharedKey.OWNER_ID,""))
        obj.put("DriverId", driverid)
        obj.put("Pi", "1")
        obj.put("Ps", "-1")
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        );
        RetrofitFactory.client
            .getgeofenceList(
                body
            )
            .enqueue(object : Callback<List<GeofenceListResponse>> {
                override fun onResponse(
                    call: Call<List<GeofenceListResponse>>,
                    response: Response<List<GeofenceListResponse>>
                ) {
                    showProgress(this@GeofenceListActivity, false)
                    if (response.isSuccessful
                    ) {
                        LogUtils.e(response.body())
                        try {
                            geofencelist = response.body() as List<GeofenceListResponse>
                            var adapter: GeofenceListAdapter? =
                                geofencelist?.let { GeofenceListAdapter(this@GeofenceListActivity,
                                    it as MutableList<GeofenceListResponse>
                                ) }
                            recyclerView?.adapter = adapter
                            adapter?.notifyDataSetChanged()
                            if (geofencelist!!.size > 0) {
                            } else {
                                ToastUtils.showShort("No Geofences Found")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(
                    call: Call<List<GeofenceListResponse>>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    showProgress(this@GeofenceListActivity, false)

                }
            })
    }

}

