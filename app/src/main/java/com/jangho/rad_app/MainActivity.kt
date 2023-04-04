package com.jangho.rad_app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.jangho.rad_app.Adapter.PageAdapter
import com.jangho.rad_app.GPSPage.GpsActivity
import com.jangho.rad_app.HomePage.Board3Fragment
import com.jangho.rad_app.HomePage.Intro1Fragment
import com.jangho.rad_app.HomePage.Notice2Fragment
import com.jangho.rad_app.MyPage.MyPageActivity
import com.jangho.rad_app.SettingPage.SettingActivity
import com.jangho.rad_app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var myId: String
    private lateinit var myPw: String
    private lateinit var myName: String
    private lateinit var myNo: String
    private lateinit var myTeam: String
    private lateinit var myPosition: String
    private lateinit var photoDir: String
    var mBackWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        
        //FCM 전송 위한 기기 token 값 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("token: ","${task.result}")
            }
        }
        val prefAuto = getSharedPreferences("autoId", 0)
        val savedId = prefAuto.getString("id", "").toString()
        val savedPw = prefAuto.getString("pw", "").toString()
        val savedName = prefAuto.getString("name", "").toString()
        val savedTeam = prefAuto.getString("team", "").toString()
        val savedPosition = prefAuto.getString("position", "").toString()
        val savedImg = prefAuto.getString("img", "").toString()
        val savedNo = prefAuto.getString("no", "").toString()
        //자동로그인시 shared 파일 읽어옴,
        if (savedId.equals("")) {
            //id 폴더에 아무런 값도 없다면 아무 일도 일어나지 않음.

        } else {//자동 로그인이면 Pref파일에서 가져온 데이터로 계속 넣어줌.
            intent.putExtra("myId", savedId)
            intent.putExtra("myPw", savedPw)
            intent.putExtra("myName", savedName)
            intent.putExtra("myTeam", savedTeam)
            intent.putExtra("PhotoDir", savedImg)
            intent.putExtra("myPosition", savedPosition)
            intent.putExtra("myNo", savedNo)
        }

        myId = intent.getStringExtra("myId").toString()
        myPw = intent.getStringExtra("myPw").toString()
        myName = intent.getStringExtra("myName").toString()
        myNo = intent.getStringExtra("myNo").toString()
        myTeam = intent.getStringExtra("myTeam").toString()
        myPosition = intent.getStringExtra("myPosition").toString()
        photoDir = intent.getStringExtra("PhotoDir").toString()

        //하단바
        binding.bottomView.setOnItemSelectedListener { bottom_menu ->
            when (bottom_menu.itemId) {
                R.id.item_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    //자기 자신으로 돌아갈 때는 다시 그대로 intent값 넘김
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    true
                }
                R.id.item_myPage -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    false
                }
                R.id.item_gps -> {
                    val intent = Intent(this, GpsActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    false
                }
                R.id.item_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    false
                }
                else -> false
            }
        }
        
        //drawer 메뉴
        //Visible 메뉴 체크 변수
        var checkVisible = false
        binding.navDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_main -> {
                    if (!checkVisible) {
                        binding.navDrawer.menu.findItem(R.id.nav_main).isChecked = true
                        binding.navDrawer.menu.setGroupVisible(R.id.nav_group_main, true)
                        checkVisible = true
                        true
                    } else {
                        binding.navDrawer.menu.findItem(R.id.nav_main).isChecked = false
                        binding.navDrawer.menu.setGroupVisible(R.id.nav_group_main, false)
                        checkVisible = false
                        binding.navDrawer.setCheckedItem(0)
                        true
                    }
                }
                R.id.nav_home1 -> {
                    binding.viewPager2.setCurrentItem(4998, false)
                    binding.drawerLayout.close()
                    false
                }
                R.id.nav_home2 -> {
                    binding.viewPager2.setCurrentItem(4999, false)
                    binding.drawerLayout.close()
                    false
                }
                R.id.nav_home3 -> {
                    binding.viewPager2.setCurrentItem(5000, false)
                    binding.drawerLayout.close()
                    false
                }
                R.id.nav_mypage -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    false
                }
                R.id.nav_gps -> {
                    val intent = Intent(this, GpsActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("myPw", myPw)
                    intent.putExtra("myName", myName)
                    intent.putExtra("myTeam", myTeam)
                    intent.putExtra("PhotoDir", photoDir)
                    intent.putExtra("myPosition", myPosition)
                    intent.putExtra("myNo", myNo)
                    startActivity(intent)
                    false
                }
                R.id.nav_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    false
                }
                else -> true
            }
        }
        binding.drawerButton.setOnClickListener {
            setDrawerLayout(binding.drawerLayout, binding.navDrawer)
        }

        val pageNum = 3
        //PageAdapter에 리스트를 등록
        val fragAdapter = PageAdapter(this, pageNum)
        //ViewPager2에 Adapter 적용
        binding.viewPager2.adapter = fragAdapter

        //초기 화면
        //첫 페이지 생성 0번 페이지 % 3 == 0 경우 0번 인덱스인 첫번째 페이지를 호출 좌우 스와이프 위해 가운데 지정
        binding.viewPager2.setCurrentItem(4998, false)
        binding.indicator.setViewPager(binding.viewPager2)
        binding.indicator.createIndicators(pageNum, 0)


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            // 탭 선택시 viewPager2의 fragment를 tab 포지션에 맞게 호출해줍니다.
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // ViewPager2에서 현재 보여지고 있는 페이지의 인덱스를 계산
                val currentPosition = binding.viewPager2.currentItem % pageNum
                // 선택된 탭의 위치를 계산
                val clickedPosition = tab!!.position
                // diff = 선택 - 현재 페이지 (currentItem % pageNum)
                val diff = clickedPosition - currentPosition
                // 선택된 탭의 위치와 현재 페이지의 위치 차이에 따라 ViewPager2의 페이지를 변경
                //만일 클릭된 페이지가 현재 페이지의 차이가 1이거나, -2(3->1)일 경우 다음 페이지로 이동
                //선택 3페이지라고 치면 인덱스 2, 1000페이지일 경우 현재페이지는 %3으로 인덱스1, 차이는 1이 된다
                //그러면 다음페이지로 넘깁니다.
                if (diff == 1 || diff == -(pageNum - 1)) {
                    binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem + 1, true)
                    //클릭된 페이지가 현재 페이지보다 -1 이거나, 2라면 이전 페이지로 이동시킴
                    //선택 1페이지라고 치면 인덱스 0, 1000페이지일 경우 현재페이지는 %3으로 인덱스 1, 차이는 -1이 된다
                } else if (diff == -1 || diff == pageNum - 1) {
                    binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem - 1, true)
                }
            }

            //탭 선택 풀릴 때 필요 X
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            //탭이 재선택 될 때 필요 X
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //fragment 포지션의 % 3값의 해당하는 값으로 indicator을 이동시킴 = fragment와 일치
                binding.indicator.animatePageSelected(position % pageNum)
                //fragment 포지션의 % 3값의 해당하는 값으로 tabLayout을 이동시킴 = fragment와 일치
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position % pageNum))
            }
        })


        //다시 보지 않기 적용 함수 noticeNo 파일에 check=true keyvalue로 저장
        fun checkNotice() {
            val pref = getSharedPreferences("noticeNo", MODE_PRIVATE)
            val edit = pref.edit() // 수정하기
            edit.putString("check", "true")// 값 넣기
            edit.apply() // 적용하기
        }

        //noticeNo 파일
        val prefID = getSharedPreferences("noticeNo", 0)
        //check키 값 읽어오기
        val checkID = prefID.getString("check", "").toString()
        //데이터 키 값 id
        //위에서 넣어준 값에 값이 있는 경우 다신 팝업 안띄움
        if (checkID.equals("")) {
            val mNoticeView = LayoutInflater.from(this)
                .inflate(R.layout.notice_pop, null)
            val mNoticeBuilder = AlertDialog.Builder(this)
                .setView(mNoticeView)
            val mNoticeDialog = mNoticeBuilder.show()
            val noticeWebView = mNoticeDialog.findViewById<WebView>(R.id.notice_view)
            val closeNotice = mNoticeDialog.findViewById<Button>(R.id.closeBtn)
            val againNoBtn = mNoticeDialog.findViewById<Button>(R.id.againNoBtn)

            noticeWebView?.setHorizontalScrollBarEnabled(false) // 가로 스크롤 막음
            noticeWebView?.settings?.javaScriptEnabled = true
            noticeWebView?.loadUrl("https://m.radcns.com")
            closeNotice?.setOnClickListener {
                mNoticeDialog.dismiss()
            }
            againNoBtn?.setOnClickListener {

                mNoticeDialog.dismiss()
                checkNotice()//다시 보지 않을 시 check true 설정
            }
        } else {
            //해당 noticeNo파일에 check값이 없을 경우 dialog는 아예 호출이 되지 않음
        }

        //앱 팝업 메시지
        fun checkMsg(check: Boolean) {
            //파일이 없으면 자동 생성 MODE_PRIVATE 통해 파일 지정
            val pref = getSharedPreferences("checkAppPush", MODE_PRIVATE)
            val edit = pref.edit() // 수정하기
            edit.putString("check", check.toString())// firebase 동작여부 true / false 저장
            edit.putString("firstYN", "Y")// 최초 앱(팝업) 실행시 무조건 Y값 넣기
            edit.apply() // 적용하기
        }

        //파이어베이스 앱동의 여부 파일
        val prefMsg = getSharedPreferences("checkAppPush", 0)
        //check키 값 읽어오기
        val checkMsgValue = prefMsg.getString("firstYN", "").toString()
        //데이터 키 값 id
        //위에서 넣어준 값에 값이 있는 경우 다신 팝업 안띄움
        if (checkMsgValue.equals("")) {
            val mPushView = LayoutInflater.from(this)
                .inflate(R.layout.two_pop, null)
            val mPushBuilder = AlertDialog.Builder(this)
                .setView(mPushView)

            val mPushDialog = mPushBuilder.show()
            val mainText = mPushDialog.findViewById<TextView>(R.id.main_text)
            val mainType = mPushDialog.findViewById<TextView>(R.id.main_type)
            val exit =
                mPushDialog.findViewById<Button>(R.id.btn1) //?????databinding 수정 안에서 또 선언해야하나
            val agreeBtn = mPushDialog.findViewById<Button>(R.id.btn2)
            mainType?.setText("앱 푸시 알림 안내")
            mainText?.setText("앱 푸시 알림을\n허용하시겠습니까 ?")
            agreeBtn?.setText("동의")
            exit?.setText("동의하지 않음")

            agreeBtn?.setOnClickListener {
                //파이어 베이스 동의 여부 checkAppPush 파일에 값 저장
                checkMsg(true)
                mPushDialog.dismiss()//이동 후 팝업 dialog 종료
            }
            exit?.setOnClickListener {
                //파이어베이스 비동의
                checkMsg(false)
                mPushDialog.dismiss()//이동 후 팝업 dialog 종료
            }
        }
        //Y 최초 실행해서 동의 경우 dialog띄우지 않음
        else {
        }

        //앱 실행 여부 저장 값 shared Preferences 통해 값 없으면 해당 dialog띄우지 않음
        val prefPermission = getSharedPreferences("permission", 0)
        val permissionFirst = prefPermission.getString("firstYN", "").toString()
        if (permissionFirst.equals("")) {
            permissionDialog()
        } else {
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }



    // 사용자 권한 Dialog
    fun permissionDialog() {
        val permissionDialogView = LayoutInflater.from(this)
            .inflate(R.layout.agree_popup, null)

        val mPermissionBuilder = AlertDialog.Builder(this)
            .setView(permissionDialogView)
            .setCancelable(false)//다이얼로그 외부 클릭시 안꺼지게
            .show()

        val agreeBtn = permissionDialogView.findViewById<Button>(R.id.agreeBtn)
        val noAgreeBtn = permissionDialogView.findViewById<Button>(R.id.noAgreeBtn)
        agreeBtn?.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                // 여러 권한을 한번에 요청
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_SCAN,//기기 검색
                    android.Manifest.permission.BLUETOOTH_CONNECT//등록된 기기와 통신
                ), 1000  // requestCode
            )
            mPermissionBuilder.dismiss()
        }
        noAgreeBtn?.setOnClickListener {
            Toast.makeText(this, "동의하지 않을 시 앱 이용이 불가합니다. 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.finishAffinity(this)//액티비티 종료
            System.exit(0); //프로세스 종료
        }
    }
    //통신 요청 응답 값에 따라 분기
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            for (result in grantResults) {
                // 요청된 권한 중 하나 이상이 거부되었을 때
                if (result == PackageManager.PERMISSION_DENIED) {
                    // 앱을 종료시킴
                    Toast.makeText(this, "동의하지 않을 시 앱 이용이 불가합니다. 앱이 종료됩니다.", Toast.LENGTH_SHORT)
                        .show()
                    ActivityCompat.finishAffinity(this)//액티비티 종료
                    System.exit(0); //프로세스 종료
                }
                else {
                    checkPermission()
                } // requestCode가 승인되면 firstYN Y 체크하여 허용이후는 다시 안나옴
            }
        }
    }

    fun checkPermission() {
        val pref = getSharedPreferences("permission", MODE_PRIVATE)
        val edit = pref.edit() // 수정하기
        edit.putString("firstYN", "Y")// 값 넣기
        edit.apply() // 적용하기
    }

    private fun setDrawerLayout(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        drawerLayout.openDrawer(Gravity.LEFT)
        drawerLayout.let {
            //왼쪽
            if (it.isDrawerOpen(GravityCompat.START)) {
                it.closeDrawer(GravityCompat.START)
            }
        }
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




