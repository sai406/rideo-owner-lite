package com.mstech.rideioliteowner.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Model.AlertsResponse
import com.mstech.rideiodriverlite.Model.SharedKey
import com.mstech.rideiodriverlite.Utils.MyUtils
import com.mstech.rideioliteowner.Adapter.AlertsAdapter
import com.mstech.rideioliteowner.R
import kotlinx.android.synthetic.main.activity_alerts.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlertsActivity : AppCompatActivity() {
    var finalstarttime: Any = ""
    var finalendtime: Any = ""
    var starttime: Long = 0
    var endtime: Long = 0
    var totaltime: Long = 0
    var value: Long = 0
    private var mYear = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var eventlist: ArrayList<String> = ArrayList<String>()
    lateinit var txtDate: TextView
    lateinit var txtTime: TextView
    lateinit var filter: FloatingActionButton
    lateinit var adapter1: ArrayAdapter<String>
    var txt1: TextView? = null
    var txt2: TextView? = null
    lateinit var vehicle: Spinner
    var vehiclelist: MutableList<String> = java.util.ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = linearLayoutManager
        alert_filter.setOnClickListener(View.OnClickListener { v ->
            showCustomDialog()
        })
        var date: Date = Date()
        getStartOfDay(date)
        if (NetworkUtils.isConnected()) {
//            getOwnerVehiclesList()
            getAlertsList(finalstarttime.toString(), finalendtime.toString())
        } else {
            ToastUtils.showShort("No Internet connection")
        }
    }

    fun getStartOfDay(date: Date) {
        val formatter: DateFormat =
            SimpleDateFormat("YYYY-MM-dd")
        var calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        finalstarttime = formatter.format(calendar.time)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        finalendtime = formatter.format(calendar.time)
        System.out.println("Start Date = " + finalstarttime)
        System.out.println("End Date = " + finalendtime)
    }


    private fun getAlertsList(starttime: String, endtime: String) {
        MyUtils.showProgress(this, true)
        var obj = JSONObject()
        obj.put("TripId", "0")
//        obj.put("FromTime", starttime)
//        obj.put("ToTIme", endtime)
        obj.put("OwnerId", SPStaticUtils.getString(SharedKey.OWNER_ID,""))
        obj.put("DriverId",0)
        obj.put("AlertTypes", ",1,2,3")
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client
            .getAlertsData(
                body
            )
            .enqueue(object : Callback<MutableList<AlertsResponse>> {
                override fun onResponse(
                    call: Call<MutableList<AlertsResponse>>,
                    response: Response<MutableList<AlertsResponse>>
                ) {
                    MyUtils.showProgress(this@AlertsActivity, false)
                    if (response.isSuccessful
                    ) {

                        try {

                            var devicelist = response.body()
                            LogUtils.e(response.body())
                            for (i in devicelist?.size!! - 1 downTo 0) {
                                if (devicelist[i].packetTypeId.equals("0")) {
                                    devicelist.removeAt(i)
                                }
                            }
                            var adapter: AlertsAdapter? =
                                devicelist.let {
                                    AlertsAdapter(
                                        this@AlertsActivity,
                                        it
                                    )
                                }
                            recyclerView?.adapter = adapter
                            adapter?.notifyDataSetChanged()
                            if (devicelist.size > 0) {
                            } else {
                                ToastUtils.showShort("No Alerts  Found")
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

                override fun onFailure(
                    call: Call<MutableList<AlertsResponse>>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    MyUtils.showProgress(this@AlertsActivity, false)

                }
            })
    }


    fun showCustomDialog() {

        val viewGroup: ViewGroup = this.findViewById(android.R.id.content)
        var dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.alerts_filter_dialog, viewGroup, false)
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        var alertDialog: AlertDialog = builder.create()
        var ok_btn: Button = dialogView.findViewById(R.id.ok)
        var cancel_btn: Button = dialogView.findViewById(R.id.cancel)
        var over_speed: CheckBox = dialogView.findViewById(R.id.over_speed)
        var engine_on: CheckBox = dialogView.findViewById(R.id.engine_on)
        var engine_off: CheckBox = dialogView.findViewById(R.id.engine_off)
        var low_battery: CheckBox = dialogView.findViewById(R.id.low_battery)
        var enter_alarm: CheckBox = dialogView.findViewById(R.id.enter_alarm)
        var exit_alarm: CheckBox = dialogView.findViewById(R.id.exit_alarm)
        var power_on: CheckBox = dialogView.findViewById(R.id.power_on)
        var power_off: CheckBox = dialogView.findViewById(R.id.power_off)
        var door_alarm: CheckBox = dialogView.findViewById(R.id.door_alarm)
        var dissamble_alarm: CheckBox = dialogView.findViewById(R.id.dissamble_alarm)
        txtDate = dialogView.findViewById(R.id.txtDate)
        txtTime = dialogView.findViewById(R.id.txtTime)
        txt2 = dialogView.findViewById(R.id.txt2)
        txt1 = dialogView.findViewById(R.id.txt1)
        vehicle = dialogView.findViewById(R.id.vehicle)
//        vehicle.adapter = adapter1
        txtDate.setOnClickListener(View.OnClickListener { v ->
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    txt1!!.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    val str_date = txt1!!.text.toString()
                    val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                    try {
                        val date: Date = formatter.parse(str_date) as Date
                        starttime = date.time
                        totaltime = endtime - starttime
                        value = 604800000
                        if (endtime != 0L) {
                            if (starttime < endtime) {
                                if (totaltime < value) {

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Select Date\nWith in 7Days ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Enter correct\n date",
                                    Toast.LENGTH_LONG
                                ).show()
                                txt2!!.text = ""
                            }
                        }
                        //
                        LogUtils.d("", "onDateSet:" + date.time)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }, mYear, mMonth, mDay
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.datePicker.minDate = mDay.toLong()
            datePickerDialog.show()

        })
        txtTime.setOnClickListener( View.OnClickListener { v ->
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    txt2!!.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    val str_date = txt2!!.text.toString()
                    val formatter: DateFormat =
                        SimpleDateFormat("dd-MM-yyyy")
                    try {
                        val date = formatter.parse(str_date) as Date
                        endtime = date.time
                        totaltime = endtime - starttime
                        value = 604800000
                        if (starttime < endtime) {
                            if (totaltime < value) {
                            } else {
                                Toast.makeText(
                                    this,
                                    "Select Date\nWith in 7Days ",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Select correct\n date",
                                Toast.LENGTH_LONG
                            ).show()
                            txt2!!.text = ""
                        }


                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        })
        ok_btn.setOnClickListener(View.OnClickListener { v ->
            if (!eventlist.isEmpty()) {
                eventlist.clear()
            }
            if (over_speed.isChecked) {
                eventlist.add("6")
            }
            if (engine_on.isChecked) {
                eventlist.add("254")
            }
            if (engine_off.isChecked) {
                eventlist.add("255")
            }
            if (low_battery.isChecked) {
                eventlist.add("14")
            }
            if (exit_alarm.isChecked) {
                eventlist.add("5")
            }
            if (power_on.isChecked) {
                eventlist.add("12")
            }
            if (power_off.isChecked) {
                eventlist.add("17")
            }
            if (door_alarm.isChecked) {
                eventlist.add("20")
            }
            if (dissamble_alarm.isChecked) {
                eventlist.add("19")
            }
            if (enter_alarm.isChecked) {
                eventlist.add("4")
            }
            if (txt1?.text!!.length > 1 && txt2?.text!!.length < 1 || txt1?.text!!.length < 1 && txt2?.text!!.length > 1 || txt1?.text!!.length < 1 && txt2?.text!!.length < 1) {
                ToastUtils.showShort("Select Start and End Dates")
            } else {
                val formatter: DateFormat =
                    SimpleDateFormat("YYYY-MM-dd")
                finalstarttime = formatter.format(starttime)
                finalendtime = formatter.format(endtime)
                alertDialog.dismiss()
                getAlertsList(finalstarttime.toString(), finalendtime.toString())
            }

        })
        cancel_btn.setOnClickListener(View.OnClickListener { v ->
            alertDialog.dismiss()
        })
        alertDialog.show()
    }
}
