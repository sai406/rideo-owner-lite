package com.mstech.rideioliteowner.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mstech.rideioliteowner.Activities.GeofenceActivity
import com.mstech.rideioliteowner.Model.GeofenceListResponse
import com.mstech.rideioliteowner.R


class GeofenceListAdapter(
    var context: Context,
    samplelist: MutableList<GeofenceListResponse>
) :
    RecyclerView.Adapter<GeofenceListAdapter.MyViewHolder>() {
    private val samplelist: MutableList<GeofenceListResponse>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.geo_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val m: GeofenceListResponse = samplelist[position] as GeofenceListResponse
        holder.geo_name.text=m.geofenceName
        holder.radius.text = m.radiusinKm
        holder.time.text = m.createdDay
        holder.address.text = m.location
        holder.delete.setOnClickListener(View.OnClickListener {
//            deleteGeofence(m.geoFenceId,position);
        })
        holder.navigation.setOnClickListener(View.OnClickListener {  context.startActivity(Intent(context, GeofenceActivity::class.java).putExtra("radius",m.radius).putExtra("lat",m.latitude).putExtra("lng",m.longitude))
        })
    }

  /*  private fun deleteGeofence(geoFenceId: Int, param: Int) {
        showProgress(context, true)
        var obj = JSONObject()
        obj.put("GeofenceId", geoFenceId)
        LogUtils.e(obj.toString())
        var body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            ((obj)).toString()
        )
        RetrofitFactory.client
            .delete_geofence(
                body
            )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    showProgress(context, false)
                    if (response.isSuccessful
                    ) {
                        try {
                            LogUtils.e(response.body()?.string())
                            if (response.body()!!.string()!!.toString() != null) {
                                ToastUtils.showShort("Geofence Deleted Successfully")
                                removeAt(param)
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
                    showProgress(context, false)

                }
            })
    }*/

    override fun getItemCount(): Int {
        return samplelist.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var geo_name: TextView
        var radius: TextView
        var time: TextView
        var address: TextView
        var navigation: ImageView
        var delete: ImageView
        //
        init {
            // get the reference of item view's
            geo_name = itemView.findViewById<View>(R.id.geo_name) as TextView
            radius = itemView.findViewById<View>(R.id.radius) as TextView
            time = itemView.findViewById<View>(R.id.time) as TextView
            address = itemView.findViewById<View>(R.id.address) as TextView
            navigation = itemView.findViewById<View>(R.id.navigation) as ImageView
            delete = itemView.findViewById<View>(R.id.delete) as ImageView
        }
    }

    init {
        this.samplelist = samplelist
    }

    fun removeAt(position: Int) {
        samplelist.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, samplelist?.size)
    }
}