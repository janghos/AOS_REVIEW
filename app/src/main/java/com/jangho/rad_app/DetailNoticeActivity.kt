package com.jangho.rad_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat

class DetailNoticeActivity : AppCompatActivity() {

    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_notice)
        val web = findViewById<WebView>(R.id.notice_view)
        val close = findViewById<Button>(R.id.closeBtn)
        //기본적으로 자바스크립트는 WebView에서 사용 중지됩니다.
        // 자바스크립트는 WebView에 연결된 WebSettings를 통해 사용 설정할 수 있습니다.
        //getSettings()로 WebSettings를 가져온 다음 setJavaScriptEnabled()로 자바스크립트를 사용 설정할 수 있습니다.
        web.settings.javaScriptEnabled = true
        web.loadUrl("https://m.radcns.com/about")

        close.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 한번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)//액티비티 종료
            System.exit(0); //프로세스 종료
        }
    }
}