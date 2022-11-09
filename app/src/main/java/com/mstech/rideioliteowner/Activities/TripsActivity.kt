package com.mstech.rideioliteowner.Activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mstech.rideiodriver.Model.GetTripsResponse
import com.mstech.rideiodriverlite.Adapter.GetTripsAdapter
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.AppConstants
import com.mstech.rideiodriverlite.Utils.MyUtils.showProgress
import com.mstech.rideioliteowner.R
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TripsActivity : AppCompatActivity() {
    lateinit var txtDate: TextView
    lateinit var txtTime: TextView
    lateinit var filter: FloatingActionButton
     var txt1: TextView? = null
     var txt2: TextView? = null
    lateinit var vehicle: Spinner
    var starttime: Long = 0
    var endtime: Long = 0
    var totaltime: Long = 0
    var value: Long = 0
    var finalstarttime: Any = 0
    var finalendtime: Any = 0
    private var mYear = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var adapter: GetTripsAdapter? = null
    var recyclerView: RecyclerView? = null
    var vehiclelist: MutableList<String> = ArrayList()
    lateinit var adapter1: ArrayAdapter<String>
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_trips)
        supportActionBar?.title = "Trips"
        getCurrentWeekDate(-1)
        filter = findViewById(R.id.filter)
        recyclerView = findViewById(R.id.recyclerview)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = linearLayoutManager
        val gsonBuilder: GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("M/d/yy hh:mm a")
        gson = gsonBuilder.create()
        if (NetworkUtils.isConnected()) {
//            getOwnerVehiclesList()
            getTrips()
        } else {
            ToastUtils.showShort("No Internet Connection")
        }

    }


    fun getCurrentWeekDate(week: Int) {
        var c: Calendar = GregorianCalendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
        c.add(Calendar.DAY_OF_WEEK, week)
        var df: DateFormat = SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.getDefault())
        var startDate: String
        var endDate: String
        startDate = df.format(c.time)
        c.add(Calendar.DAY_OF_MONTH, 6)
        endDate = df.format(c.time)
        finalstarttime = startDate
        finalendtime = endDate
        txt1?.text = startDate
        txt2?.text = endDate
        System.out.println("Start Date = " + startDate)
        System.out.println("End Date = " + endDate)
    }

    fun getStartOfDay(date: Date) {
        val formatter: DateFormat =
            SimpleDateFormat("MM-dd-yyyy hh:mm a")
        var calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        finalstarttime = formatter.format(calendar.time)
        txt1?.text = finalstarttime.toString()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        finalendtime = formatter.format(calendar.time)
        txt2?.text = finalendtime.toString()
        System.out.println("Start Date = " + finalstarttime)
        System.out.println("End Date = " + finalendtime)
    }

    fun getTrips() {
        val formatter: DateFormat =
            SimpleDateFormat("YYYY-MM-dd")
        val calendar = Calendar.getInstance()
        val today = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        val todayAsString: String = formatter.format(today)
        val tomorrowAsString: String = formatter.format(tomorrow)
//        if (starttime != endtime) {
//            finalstarttime = formatter.format(starttime)
//            finalendtime = formatter.format(endtime)
//        }
        var imei = "0"
        if (::vehicle.isInitialized) {
            imei = vehicle.selectedItem.toString()
        }
        LogUtils.d("", "onDateSet:" + finalstarttime + ",,," + finalendtime)
        var params = JSONObject()
        params.put("OwnerId",  SPStaticUtils.getString(SharedKey.OWNER_ID,""))
        params.put("DriverId", 0)
//        params.put("FromTime", "2020-05-30")
//        params.put("ToTime", "tomorrowAsString")
        params.put("Imei", imei)
        Log.d("", "params " + params.toString())
        showProgress(this, true)
        var mRequestBody = params.toString()
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            AppConstants.GET_TRIPS,
            com.android.volley.Response.Listener { response ->
                Log.d("response", response)
                showProgress(this, false)
                var tripslist: MutableList<GetTripsResponse> = mutableListOf()
                try {
                    val gson = Gson()
                    val arrayTutorialType =
                        object : TypeToken<MutableList<GetTripsResponse>>() {}.type
                    tripslist.clear()
                    tripslist = gson.fromJson(response, arrayTutorialType)
                    adapter = GetTripsAdapter(this, tripslist)
                    recyclerView!!.adapter = adapter
                    if (tripslist.size > 0) {
                    } else {
                        ToastUtils.showShort("No Trips Found")
                    }
                    adapter?.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Log.e("LOG_VOLLEY", error.toString())
                showProgress(this, false)
                ToastUtils.showShort(error.toString())

            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                return try {
                    mRequestBody.toByteArray(charset("utf-8"))
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf(
                        "Unsupported Encoding while trying to get the bytes of %s using %s",
                        mRequestBody,
                        "utf-8"
                    )
                    null
                }
            }
        }
        requestQueue.add(stringRequest)


    }



}
