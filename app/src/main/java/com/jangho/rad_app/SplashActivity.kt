package com.jangho.rad_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.jangho.rad_app.Model.ResponseVersion
import com.jangho.rad_app.Service.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {

    private lateinit var ckVersion : String
    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val versionList = mutableListOf<Int>()
        //api 버전
        val newVersionList = mutableListOf<Int>()
        //버전 확인 Dialog
        val curVersion = BuildConfig.VERSION_NAME//현재 버전 gralde
        versionList.add(curVersion.split(".")[0].toInt())
        versionList.add(curVersion.split(".")[1].toInt())
        versionList.add(curVersion.split(".")[2].toInt())
        Log.e("package", packageName)

        //retrofit getVersion의 매개변수 Query를 통해 ?dataCode=3의 주소로 URL을 get타입으로 요청
        //받을 매개변수 값인 ResposneVersion 데이터 클래스 타입을 선언함
        val call: Call<ResponseVersion> =
            ServiceCreator.RetrofitService.getVersion("3")

        call.enqueue(object : Callback<ResponseVersion> { 
            override fun onResponse(
                call: Call<ResponseVersion>,
                response: Response<ResponseVersion>
            ) { //respose 응답값 성공시,
                if (response.isSuccessful) {
                    ckVersion = response.body()!!.data.AOSMinVersion.toString()
                    val splitCkVersion = ckVersion.split(".")

                    if(splitCkVersion.size == 3 && splitCkVersion.any{ it.matches("[0-9]+".toRegex())}) {
                        newVersionList.add(ckVersion.split(".")[0].toInt())
                        newVersionList.add(ckVersion.split(".")[1].toInt())
                        newVersionList.add(ckVersion.split(".")[2].toInt())
                    }
                    else{
                        Toast.makeText(baseContext,"서버의 버전 정보를 확인해주세요( 관리자 ) ",Toast.LENGTH_SHORT).show()
                        ActivityCompat.finishAffinity(parent)//액티비티 종료
                        System.exit(0); //프로세스 종료
                    }
                }
            }
            //response.isSuccessful이 false이거나 body()에 값이 없을 경우 에러 처리
            // => 서버통신 실패
            override fun onFailure(call: Call<ResponseVersion>, t: Throwable) {
                Toast.makeText(baseContext,"버전 정보 받아오기 실패입니다! 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }
        })
        val appPackageName = "com.leadon.pacificocean"
        /*val appPackageName = packageName
        Log.e("app_key", appPackageName)//현재 패키지가 나옴*/

        Handler().postDelayed({
            if(newVersionList.isNotEmpty()){//api 버전 못 받아올시 null 값 앱 종료
            //현재 버전과 신버전 1,2번째 자리 비교하여 둘 중 하나라도 작을시 if문 탐
                if ( versionList[0] < newVersionList[0] || versionList[1] < newVersionList[1]){
                    //현재 버전이 api 버전보다 첫번째 자리가 더 작은 경우 강제 업데이트
                    if (versionList[0] < newVersionList[0] ) {
                        val mDialogView = LayoutInflater.from(this)
                            .inflate(R.layout.two_pop, null)
                        val mBuilder = AlertDialog.Builder(this)
                            .setView(mDialogView)
                            .setCancelable(false)//다이얼로그 외부 클릭시 안꺼지게
                        val mAlertDialog = mBuilder.show()
                        val mainText = mAlertDialog.findViewById<TextView>(R.id.main_text)
                        val exit =
                            mAlertDialog.findViewById<Button>(R.id.btn1) //?????databinding 수정 안에서 또 선언해야하나
                        val marketBtn = mAlertDialog.findViewById<Button>(R.id.btn2)
                        mainText?.setText("강제 업데이트가 필요합니다.\n마켓으로 이동하시겠습니까 ?")
                        exit?.setText("종료")
                        marketBtn?.setOnClickListener { //마켓이동
                            //웹 브라우저 호출 위해 ACTION_VIEW
                            Toast.makeText(this,"등록 안된 해당 패키지 이름, 마켓 이동이 안됨-> $appPackageName",Toast.LENGTH_SHORT).show()
                            val intent = Intent(Intent.ACTION_VIEW).apply {//apply 통해서 intent에 한번에 데이터를 넣어줌
                                //Uri = 리소스 구분 식별자 - 접근하는 리소스 어디있는지, 인터넷 모바일 기기등 다양한 곳 사용
                                //uri.parse 접근할 주소 입력하면 텍스트 파싱하여 해당 uri로 연결해줌
                                data =
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                    setPackage("com.android.vending")//구글 플레이스토어에서 앱의 세부 정보를 볼 수 있도록 인텐트 패키지를 android 패키지
                            }
                            startActivity(intent)
                        }
                        exit?.setOnClickListener {
                            ActivityCompat.finishAffinity(this)//액티비티 종료
                            System.exit(0); //프로세스 종료
                        }
                    //현재 버전이 api 버전보다 첫째 자리는 같고,두번째 자리만 더 작은 경우 선택 업데이트
                    }else if (versionList[1] < newVersionList[1]  && versionList[0] == newVersionList[0]) {
                        val mDialogView = LayoutInflater.from(this)
                            .inflate(R.layout.two_pop, null)
                        val mBuilder = AlertDialog.Builder(this)
                            .setView(mDialogView)
                            .setCancelable(false)//다이얼로그 외부 클릭시 안꺼지게
                        val mAlertDialog = mBuilder.show()
                        val exit =
                            mAlertDialog.findViewById<Button>(R.id.btn1) //?????databinding 수정 안에서 또 선언해야하나
                        val marketBtn = mAlertDialog.findViewById<Button>(R.id.btn2)
                        //웹 브라우저 호출 위해 Intent의 ACTION_VIEW에 속성 값 넣어줌
                        marketBtn?.setOnClickListener { //마켓이동
                            val intent = Intent(Intent.ACTION_VIEW).apply {//apply 통해서 intent에 한번에 데이터를 넣어줌
                                //Uri = 리소스 구분 식별자 - 접근하는 리소스 어디있는지, 인터넷 모바일 기기등 다양한 곳 사용
                                //uri.parse 접근할 주소 입력하면 텍스트 파싱하여 해당 uri로 연결해줌
                                data =
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                setPackage("com.android.vending")//구글 플레이스토어에서 앱의 세부 정보를 볼 수 있도록 인텐트 패키지를 android 패키지
                            }
                            startActivity(intent)
                        }
                        exit?.setOnClickListener {
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                    }
                    else {//셋째 자리만 낮을 시 그냥 실행
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                    }
                }
                //버전 1,2 번째 숫자 모두 높을 경우 버전 업데이트 필요 없게 구현함
                else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                }
            }else{
                Log.e("dd",newVersionList.toString() )
                Toast.makeText(this, "버전 정보 읽어오기 실패 앱을 다시 실행해주세요!", Toast.LENGTH_SHORT).show()
                ActivityCompat.finishAffinity(this)//액티비티 종료
                System.exit(0); //프로세스 종료
            }
        },3000)
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