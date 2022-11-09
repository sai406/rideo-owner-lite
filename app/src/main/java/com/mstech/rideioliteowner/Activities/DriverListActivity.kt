package com.mstech.rideioliteowner.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.MyUtils
import com.mstech.rideioliteowner.Adapter.AlertsAdapter
import com.mstech.rideioliteowner.Adapter.DriverListAdapter
import com.mstech.rideioliteowner.Model.DriverListResponse
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_alerts.*
import kotlinx.android.synthetic.main.activity_driver_list.*
import kotlinx.android.synthetic.main.activity_driver_list.recyclerView
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_list)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = linearLayoutManager
        add_driver.setOnClickListener(View.OnClickListener {
startActivity(Intent(this,AddDriverActivity::class.java).putExtra("driverid",0))
        })
    }
    private fun getDriverList() {
        MyUtils.showProgress(this, true)
        var obj = JSONObject()
        obj.put("TripId", "0")
        obj.put("OwnerId", SPStaticUtils.getString(SharedKey.OWNER_ID,""))
        obj.put("FromTime","")
        obj.put("ToTime", "")
        obj.put("Pi", "1")
        obj.put("Ps", "-1")
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client
            .getdriverlist(
                body
            )
            .enqueue(object : Callback<MutableList<DriverListResponse>> {
                override fun onResponse(
                    call: Call<MutableList<DriverListResponse>>,
                    response: Response<MutableList<DriverListResponse>>
                ) {
                    MyUtils.showProgress(this@DriverListActivity, false)
                    if (response.isSuccessful
                    ) {

                        try {

                            var devicelist = response.body()
                            LogUtils.e(response.body())
                            var adapter: DriverListAdapter? =
                                devicelist.let {
                                    it?.let { it1 ->
                                        DriverListAdapter(
                                            this@DriverListActivity,
                                            it1
                                        )
                                    }
                                }
                            recyclerView?.adapter = adapter
                            adapter?.notifyDataSetChanged()
                            if (devicelist!!.size > 0) {
                            } else {
                                ToastUtils.showShort("No Drivers  Found")
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

                override fun onFailure(
                    call: Call<MutableList<DriverListResponse>>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    MyUtils.showProgress(this@DriverListActivity, false)

                }
            })
    }

    override fun onStart() {
        super.onStart()
        if (NetworkUtils.isConnected()) {
            getDriverList()
        } else {
            ToastUtils.showShort("No Internet connection")
        }
    }
}
