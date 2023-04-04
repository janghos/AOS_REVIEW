package com.jangho.rad_app.SettingPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.jangho.rad_app.*
import com.jangho.rad_app.databinding.ActivitySettingBinding
import java.text.SimpleDateFormat
import java.util.*

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding
    var mBackWait: Long = 0
    //인텐트 받아오는 값
    private lateinit var myId: String
    private lateinit var myPw: String
    private lateinit var myName: String
    private lateinit var myNo: String
    private lateinit var myTeam: String
    private lateinit var myPosition: String
    private lateinit var photoDir: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myId = intent.getStringExtra("myId").toString()
        myPw = intent.getStringExtra("myPw").toString()
        myName = intent.getStringExtra("myName").toString()
        myNo = intent.getStringExtra("myNo").toString()
        myTeam = intent.getStringExtra("myTeam").toString()
        myPosition = intent.getStringExtra("myPosition").toString()
        photoDir = intent.getStringExtra("PhotoDir").toString()

        checkPush()
        checkLogin()


        //설정 창 나가기
        binding.closeBtn.setOnClickListener { finish() }

        binding.loginSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveLoginData(myId, myPw, myName, myTeam, myPosition, photoDir, myNo)
            } else {
                removeLoginData()
            }
        }
        binding.pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changePush(true)
            } else {
                changePush(false)
            }
        }
        binding.versionName.append(BuildConfig.VERSION_NAME)

        //로그아웃
        binding.logoutBtn.setOnClickListener {
            finish()
            removeLoginData()//자동 로그인 데이터 삭제
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
        }
        //shared는 앱 데이터 삭제하면 목록 사라짐 별도 데이터베이스 관리 필요
        binding.pushList.setOnClickListener{
            val intent = Intent(this, DetailPushActivity::class.java)
            startActivity(intent)
        }
        binding.connectReport.setOnClickListener{
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }

    fun saveLoginData(
        autoId: String,
        autoPw: String,
        autoName: String,
        autoTeam: String,
        autoPosition: String,
        autoImg: String,
        autoNo: String
    ) {
        val pref = getSharedPreferences("autoId", MODE_PRIVATE)
        val edit = pref.edit() // 수정하기
        edit.putString("id", autoId)
        edit.putString("pw", autoPw)
        edit.putString("name", autoName)
        edit.putString("team", autoTeam)
        edit.putString("position", autoPosition)
        edit.putString("img", autoImg)
        edit.putString("no", autoNo)
        edit.apply()
    }
    fun removeLoginData(){
        val pref = getSharedPreferences("autoId", MODE_PRIVATE)
        val edit = pref.edit() // 수정하기
        edit.clear()
        edit.apply()
    }
    fun changePush(status: Boolean){
        //이쪽은 파일에서 id 파일 값 읽어오기
        val pref = getSharedPreferences("checkAppPush", MODE_PRIVATE)
        val edit = pref.edit() // 수정하기
        edit.putString("check", status.toString())// fcm 동작여부 true / false 저장
        edit.apply()
    }
    fun checkLogin(){
        val prefID = getSharedPreferences("autoId", 0)
        val savedId = prefID.getString("id", "").toString()
        binding.loginSwitch.isChecked = !savedId.equals("")
    }

    fun checkPush(){
        val prefApp = getSharedPreferences("checkAppPush", 0)
        val checkValue = prefApp.getString("check", "").toString()
        binding.pushSwitch.isChecked = !checkValue.equals("false")
    }
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)//액티비티 종료
            System.exit(0); //프로세스 종료
        }
    }
}