package com.jangho.rad_app.HomePage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jangho.rad_app.Adapter.BoardRVAdapter
import com.jangho.rad_app.DetailBoardActivity
import com.jangho.rad_app.DetailNoticeActivity
import com.jangho.rad_app.Model.BoardData
import com.jangho.rad_app.Model.NoticeData
import com.jangho.rad_app.R
import org.json.JSONArray

class Board3Fragment : Fragment() {


    private lateinit var v : View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("fraglife", "onCreate()" )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_board3, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonString = view.context.assets.open("board.json").reader().readText()

        // 2. JSONArray 로 파싱
        val jsonArray = JSONArray(jsonString)
        val boardList = mutableListOf<BoardData>()

        // 3. JSONArray 순회: 인덱스별 JsonObject 취득후, key에 해당하는 value 확인
        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            val title = jsonObject.getString("title")
            val writer = jsonObject.getString("writer")
            val content = jsonObject.getString("content")
            val date = jsonObject.getString("date")
            boardList.add(BoardData(title, writer, content,date))

        }
        val topBtn = v.findViewById<Button>(R.id.topBtn)
        val rv = v.findViewById<RecyclerView>(R.id.board_rv)
        val rvAdapter = BoardRVAdapter(boardList)

        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(context)

        //리사이클러뷰는 아이템 클릭을 따로 정의해야함
        rvAdapter.itemClick = object : BoardRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val titleSend = boardList[position].title
                val writerSend = boardList[position].writer
                val contentSend = boardList[position].content
                val dateSend = boardList[position].date

                val intent = Intent(activity, DetailBoardActivity::class.java)

                intent.putExtra("title", titleSend)
                intent.putExtra("writer", writerSend)
                intent.putExtra("content", contentSend)
                intent.putExtra("date", dateSend)

                startActivity(intent)
            }
        }
        //이건 recyclerView를 반복 출력하는 레이아웃이므로 onClick 정의되어 있음
        topBtn.setOnClickListener {
            rv.smoothScrollToPosition( 0 )
        }
    }

}