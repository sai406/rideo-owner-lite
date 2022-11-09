package com.mstech.rideioliteowner.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.mstech.rideiodriver.Utils.RetrofitFactory
import com.mstech.rideiodriverlite.Utils.MyUtils
import com.mstech.rideioliteowner.Activities.AddDriverActivity
import com.mstech.rideioliteowner.Activities.GeofenceActivity
import com.mstech.rideioliteowner.Activities.GeofenceListActivity
import com.mstech.rideioliteowner.Model.DriverListResponse
import com.mstech.rideioliteowner.R
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DriverListAdapter(
    var context: Context,
    samplelist: MutableList<DriverListResponse>
) :
    RecyclerView.Adapter<DriverListAdapter.MyViewHolder>() {
    private val samplelist: MutableList<DriverListResponse>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.driver_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val m: DriverListResponse = samplelist[position]
        holder.driver_name.text=m.driverName
        holder.email.text=m.email
        holder.mobile.text = m.mobile
        holder.address.text = m.address
        holder.geofence.setOnClickListener(View.OnClickListener { v ->
            val i = Intent(context, GeofenceListActivity::class.java)
            i.putExtra("driverid",m.driverId)
            context.startActivity(i)
        })
        holder.update.setOnClickListener(View.OnClickListener { v ->
            val i = Intent(context, AddDriverActivity::class.java)
            i.putExtra("driverid",m.driverId)
            context.startActivity(i)
        })
        holder.speed.setOnClickListener(View.OnClickListener { v ->
            showSpeedLimit(m.driverId)
        })
    }

    override fun getItemCount(): Int {
        return samplelist.size
    }
    fun showSpeedLimit(driverId: Int) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_speed, null)
        dialogBuilder.setView(dialogView)
        val edt = dialogView.findViewById<View>(R.id.edit1) as EditText
        dialogBuilder.setMessage("Enter Speed Limit below")
        dialogBuilder.setPositiveButton("Done",
            DialogInterface.OnClickListener { dialog, whichButton ->
                //do something with edt.getText().toString();
                if (edt.text.length>0){
                    setSpeedLimit(driverId,edt.text.toString());
                }else{
                    ToastUtils.showShort("Enter Speed Limit")
                }
            })
        dialogBuilder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, whichButton ->
                //pass
            })
        val b: AlertDialog = dialogBuilder.create()
        b.show()
    }

    private fun setSpeedLimit(driverId: Int, speedlimit: String) {
        MyUtils.showProgress(context, true)
        var obj = JSONObject()
        obj.put("Speed",speedlimit )
        obj.put("DriverId",driverId)
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client
            .setSpeedLimit(
                body
            )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    MyUtils.showProgress(context, false)
                    if (response.isSuccessful
                    ) {
                        try {
                            LogUtils.e(response.body()?.string())
                            if (response.body()!!.string()!!.toString() != null) {
                                ToastUtils.showShort("Speed Limit Added Successfully")
                            }else{
                                ToastUtils.showShort("Please try again")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    ToastUtils.showShort(t.message)
                    MyUtils.showProgress(context, false)

                }
            })

    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var driver_name: TextView
        var mobile: TextView
        var email: TextView
        var address: TextView
        var geofence: RelativeLayout
        var speed: RelativeLayout
        var update: RelativeLayout
        //
        init {
            // get the reference of item view's
            driver_name = itemView.findViewById<View>(R.id.driver_name) as TextView
            mobile = itemView.findViewById<View>(R.id.mobile) as TextView
            email = itemView.findViewById<View>(R.id.email) as TextView
            address = itemView.findViewById<View>(R.id.address) as TextView
            geofence = itemView.findViewById<View>(R.id.geofence) as RelativeLayout
            speed = itemView.findViewById<View>(R.id.speed) as RelativeLayout
            update = itemView.findViewById<View>(R.id.update) as RelativeLayout
        }
    }

    init {
        this.samplelist = samplelist
    }
}