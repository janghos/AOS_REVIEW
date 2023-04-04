package com.jangho.rad_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.jangho.rad_app.HomePage.Board3Fragment
import com.jangho.rad_app.databinding.ActivityDetailBoardBinding

class
DetailBoardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBoardBinding

    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_board)
        if(intent.hasExtra("title")){
            binding.title.setText(intent.getStringExtra("title"))
        }
        if(intent.hasExtra("writer") && intent.hasExtra("date")){
            val writeDate = intent.getStringExtra("writer") + "( 작성일 : " + intent.getStringExtra("date") + ")"
            binding.detailWriterDate.setText(writeDate)
        }
        if(intent.hasExtra("content")){
            binding.detailContent.setText(intent.getStringExtra("content"))
        }
        binding.closeBtn.setOnClickListener {
            finish()
        }
    }
    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 한번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)//액티비티 종료
            System.exit(0); //프로세스 종료
        }
    }

}