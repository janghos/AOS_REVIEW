package com.jangho.rad_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.jangho.rad_app.Model.PushModel
import com.jangho.rad_app.R

class PushAdapter (val List : MutableList<PushModel>) : BaseAdapter(){
    override fun getCount(): Int {
        return List.size
    }

    override fun getItem(p0: Int): Any {
        return List[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var convertView = p1

        if(convertView == null){
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.push_item, p2, false)
        }

        val pushDate = convertView!!.findViewById<TextView>(R.id.pushDate)
        val pushMessage = convertView!!.findViewById<TextView>(R.id.pushMessage)
        pushDate.text = List[p0].pushDate.substring(0,8)
        pushMessage.text = List[p0].pushMessage

        return convertView!!
    }
}