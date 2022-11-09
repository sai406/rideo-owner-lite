package com.mstech.rideiodriverlite.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mstech.rideiodriver.Model.GetTripsResponse
import com.mstech.rideioliteowner.Activities.PlayBackActivity
import com.mstech.rideioliteowner.R

class GetTripsAdapter(var context: Context, samplelist: MutableList<GetTripsResponse>) : RecyclerView.Adapter<GetTripsAdapter.MyViewHolder>() {
    private val samplelist: MutableList<GetTripsResponse>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val m: GetTripsResponse = samplelist[position]
        holder.avgspeed.text=m.avgSpeedInKmph
        holder.start_address.text = m.startAddress
        holder.end_address.text = m.endAddress
        holder.vehiclename.text = m.vehicleNumber
        holder.distancevalue.text = m.distanceInKm
        holder.idletime.text = m.stopTime
        holder.enddate.text = m.endDay
        holder.startdate.text = m.startDay
        holder.playback.setOnClickListener(View.OnClickListener { v ->
            val i = Intent(context, PlayBackActivity::class.java)
            i.putExtra("tripid",m.tripId.toString())
            context.startActivity(i)
        })
//        holder.alerts.setOnClickListener(View.OnClickListener { v ->
//            val i = Intent(context, TripAlertsActivity::class.java)
//            i.putExtra("tripid",m.tripId.toString())
//            context.startActivity(i)
//        })
    }

    override fun getItemCount(): Int {
        return samplelist.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var start_address: TextView
        lateinit var end_address: TextView
       lateinit  var vehiclename: TextView
       lateinit var distancevalue: TextView
       lateinit var idletime: TextView
       lateinit var avgspeed: TextView
       lateinit var startdate: TextView
       lateinit var enddate: TextView
       lateinit var playback: LinearLayout
       lateinit var alerts: LinearLayout
        //
        init {
            // get the reference of item view's
            start_address = itemView.findViewById<View>(R.id.start_address) as TextView
            end_address = itemView.findViewById<View>(R.id.end_address) as TextView
            vehiclename = itemView.findViewById<View>(R.id.vehiclename) as TextView
            distancevalue = itemView.findViewById<View>(R.id.distancevalue) as TextView
            idletime = itemView.findViewById<View>(R.id.idletime) as TextView
            avgspeed = itemView.findViewById<View>(R.id.avgspeed) as TextView
            startdate = itemView.findViewById<View>(R.id.startdate) as TextView
            enddate = itemView.findViewById<View>(R.id.enddate) as TextView
            alerts = itemView.findViewById<View>(R.id.alerts) as LinearLayout
            playback = itemView.findViewById<View>(R.id.playback) as LinearLayout
        }
    }

    init {
        this.samplelist = samplelist
    }
}
