package com.jangho.rad_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jangho.rad_app.Model.BoardData
import com.jangho.rad_app.R

class BoardRVAdapter(val items : MutableList<BoardData>) : RecyclerView.Adapter<BoardRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item,parent,false)// 레이아웃 rv_item.xml 을 가져옴

        return ViewHolder(view)// rv_item.xml을 반환해줌
    }

    interface ItemClick {
        fun onClick(view : View, position: Int)
    }

    //itemClick null 일 수도 있는 값 (클릭이 되지 않아도 실행되어야 하니)
    //MainActivity에서 override 해야하니까 var로 선언해야함
    var itemClick :ItemClick ?= null

    //뷰바인딩
    override fun onBindViewHolder(holder: BoardRVAdapter.ViewHolder, position: Int) {
        if(itemClick != null){
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v,position)
            }

        }
        holder.bindItems(items[position])
    }

    //전체 리사이클러뷰의 갯수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        //변수를 rv_item.xml 에 넣어서 text 속성 변경
        fun bindItems(item : BoardData) {
            val title = itemView.findViewById<TextView>(R.id.title)
            val writer = itemView.findViewById<TextView>(R.id.writer)
            val content = itemView.findViewById<TextView>(R.id.content)
            title.text = item.title
            writer.text = item.writer + "      " + item.date
            content.text = item.content
        }
    }

}