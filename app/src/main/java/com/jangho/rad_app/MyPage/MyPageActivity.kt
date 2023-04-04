package com.jangho.rad_app.MyPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.jangho.rad_app.MainActivity
import com.jangho.rad_app.R
import com.jangho.rad_app.databinding.ActivityMyPageBinding
import com.journeyapps.barcodescanner.BarcodeEncoder

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMyPageBinding

    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        val myName = intent.getStringExtra("myName")
        val myTeam = intent.getStringExtra("myTeam")
        val myClass = intent.getStringExtra("myPosition")
        val photoDir = intent.getStringExtra("PhotoDir")
        val myNo = intent.getStringExtra("myNo")

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        Log.e("dd",intent.getStringExtra("myName").toString())
        val photoUrl = "https://hello.radcns.com/" + photoDir
        Glide.with(this)
            .load(photoUrl)
            .error("@drawable/error_image")
            .into(binding.myImg)

        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(myNo, BarcodeFormat.CODE_128,200, 100)

        Glide.with(this)
            .load(bitmap)
            .error("@drawable/error_img")
            .into(binding.myBarcode)
        binding.barcodeText.setText(myNo)
        binding.myName.append(myName)
        binding.myTeam.append(myTeam)
        binding.myClass.append(myClass)

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