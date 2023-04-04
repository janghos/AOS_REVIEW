package com.jangho.rad_app.HomePage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import com.jangho.rad_app.Adapter.ListViewAdapter
import com.jangho.rad_app.DetailNoticeActivity
import com.jangho.rad_app.Model.NoticeData
import com.jangho.rad_app.R
import org.json.JSONArray


class Notice2Fragment : Fragment() {

    private lateinit var v : View

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.e("fraglife", "onCreate()" )
        }



        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            v = inflater.inflate(R.layout.fragment_notice2, container, false)

            return v
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val jsonString = view.context.assets.open("notice.json").reader().readText()
            // 2. JSONArray 로 파싱
            val jsonArray = JSONArray(jsonString)
            val noticeList = mutableListOf<NoticeData>()

            // 3. JSONArray 순회: 인덱스별 JsonObject 취득후, key에 해당하는 value 확인
            for (index in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(index)
                val title = jsonObject.getString("title")
                val label = jsonObject.getString("label")
                val date = jsonObject.getString("date")

                noticeList.add(NoticeData(title, label, date))
            }
            val topBtn = v.findViewById<Button>(R.id.topBtn)
            val noticeListView = v.findViewById<ListView>(R.id.notice_list)
            val noticeAdapter = ListViewAdapter(noticeList)

            noticeListView.adapter = noticeAdapter
            noticeListView.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(activity, DetailNoticeActivity::class.java)
                startActivity(intent)
            }
            topBtn.setOnClickListener {
                noticeListView.smoothScrollToPosition( 0 )
            }
        }
    }