package com.yadu.imageapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yadu.weatherapp.R
import com.yadu.weatherapp.data.model.Daily
import kotlinx.android.synthetic.main.item_daily.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DailyAdapter(val context: Context?): RecyclerView.Adapter<DailyAdapter.DailyItemViewHolder>() {
    var dailyList:ArrayList<Daily> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyItemViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view: View

        view = layoutInflater.inflate(R.layout.item_daily, parent, false)
        return DailyItemViewHolder(
            view
        )

    }

    override fun onBindViewHolder(holder: DailyItemViewHolder, position: Int) {
        context?.let { (holder as DailyItemViewHolder).bind(dailyList[position], it) }
    }


    override fun getItemCount(): Int {
        return dailyList.size
    }

    class DailyItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun bind(daily: Daily?, context: Context){
            itemView.temp_daily.text = "max ${daily?.temp?.max} °C  min ${daily?.temp?.min} °C"
            itemView.humidity_daily.text = "Humidity: ${daily?.humidity} %"
            //itemView.sunrise.text = "Sunrise: ${convertUTCToTime(daily?.sunrise, )} %"
            itemView.description.text = "${daily?.weather!!.get(0).description}"
            itemView.date.text = "${convertUTCToDate(daily?.dt)}"

        }

        fun convertUTCToDate(time:Int):String{
            val t = time.toLong()*1000
            val d = Date(t.toLong())
            val itemDateStr: String = SimpleDateFormat("dd-MMM yy").format(d)
            return itemDateStr
        }

        fun convertUTCToTime(time:Int, offset:Int):String{
            val d = Date(time.toLong()*1000)
            val itemTimeStr: String = SimpleDateFormat("HH:mm").format(d)
            return itemTimeStr
        }

    }

    fun setDailyListData(dailyList:ArrayList<Daily>){
        this.dailyList = dailyList
        notifyDataSetChanged()
    }
}