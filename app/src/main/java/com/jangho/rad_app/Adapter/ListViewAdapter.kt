package com.jangho.rad_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.jangho.rad_app.Model.NoticeData
import com.jangho.rad_app.R

class ListViewAdapter(val List : MutableList<NoticeData>) : BaseAdapter() {
    override fun getCount(): Int {
        return List.size
    }

    override fun getItem(position: Int): Any {
        return List[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if(convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.listview_item, parent, false)
        }

        var label = convertView!!.findViewById<TextView>(R.id.label)
        label.text = List[position].label

        var title = convertView!!.findViewById<TextView>(R.id.title)
        title.text = List[position].title

        var date = convertView!!.findViewById<TextView>(R.id.date)
        date.text = List[position].date

        return convertView!!
    }
}