package com.jangho.rad_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jangho.rad_app.Adapter.PushAdapter
import com.jangho.rad_app.Model.PushModel
import com.jangho.rad_app.databinding.ActivityDetailPushBinding
import com.jangho.rad_app.databinding.ActivitySettingBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailPushActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailPushBinding
    private var pushCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPushBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeBtn.setOnClickListener {
            finish()
        }

        val pushList = mutableListOf<PushModel>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val oneMonthAgo = SimpleDateFormat("yyyyMMdd").format(calendar.time)
        val databaseReference = FirebaseDatabase.getInstance("https://rad-project-cade4-default-rtdb.firebaseio.com/")
        val messageReference = databaseReference.getReference("push")
        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                /*if(pushCount < 1){*/
                    for (data in snapshot.children) {
                        val time = data.child("time").value.toString()
                        // 현재 날짜보다 1달 이전인 데이터를 삭제합니다.
                        Log.e("time",time.toString())
                        if(time == "null"){//null
                            return
                        }
                        if (time.substring(0,8).toInt() < oneMonthAgo.toInt()) {
                            data.ref.removeValue()
                        } else {
                            val title = data.child("title").value.toString()
                            val message = data.child("message").value.toString()
                            // 데이터 사용
                            Log.d("firebaseData", "Title: $title, Message: $message, Time: $time")
                            pushList.add(PushModel(time, title, message))
                        }
                    }
                    // 정렬하여 어댑터에 적용합니다.
                    val dateDesList = pushList.sortedByDescending { it.pushDate }
                    val pushAdapter = PushAdapter(dateDesList as MutableList<PushModel>)
                    binding.pushListView.adapter = pushAdapter
                   /* pushCount++*/
                /*}
                else {

                }*/
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseData", "Failed to read value.", error.toException())
            }
        })
    }
}