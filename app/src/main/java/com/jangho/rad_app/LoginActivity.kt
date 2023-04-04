package com.jangho.rad_app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.jangho.rad_app.Model.RequestLoginData
import com.jangho.rad_app.Model.ResponseLoginData
import com.jangho.rad_app.MyPage.MyPageActivity
import com.jangho.rad_app.Service.ServiceCreator
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class LoginActivity : AppCompatActivity() {
    //정적 멤버 static과 비슷
    //static 다른점 companion object로 객체이다 보니 변수에 할당 가능 //ex companion object ABC
    //암호화 외부 함수 encryptCBC 에 해당하는 값 사용하기 위해 정적으로 호출
    companion object {
        const val SECRET_KEY = "LDtyvldahfwkehdfhrmdlsafrialeDiA" //AES256 32바이트
        val SECRET_IV = ByteArray(16)
    }
    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


            val idPattern ="^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]" //영문, 숫자 정규식
            val pwPattern ="^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]" // 영문, 숫자, 특수문자 정규식
            val loginId = findViewById<EditText>(R.id.radId)
            val loginPw = findViewById<EditText>(R.id.radPw)
            val loginBtn = findViewById<Button>(R.id.loginBtn)
            val autoLogin = findViewById<CheckBox>(R.id.autoLogin)
            val patternID = Pattern.compile(idPattern)//아이디 패턴을 컴파일하여 Pattern 라이브러리 등록
            val patternPW = Pattern.compile(pwPattern)//마찬가지 패스워드 정규식 Pattern 라이브러리 등록
            //아이디 기본 포커스 주기
            loginId.requestFocus()
            //패스워드 엔터 누를시 로그인 버튼 클릭
            loginPw.setOnKeyListener { view, keyCode, keyEvent ->
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    loginBtn.callOnClick()
                }
                false
            }

            //자동 로그인
            //device file /../shared 있는 id란 데이터 불러오기(디바이스의 앱 데이터 안에 넣기 때문에 같은 기기 계속 로그인 가능)
            //해당 id는 로그인 성공시 자동 로그인 체크박스 체크된 상태에서 파일 저장 이루어짐

            //이쪽은 파일에서 id 파일 값 읽어오기
            val prefID = getSharedPreferences("autoId", 0)
            //데이터 키 값 id
            val savedId = prefID.getString("id", "").toString()
            val savedPw = prefID.getString("pw", "").toString()
            val savedName = prefID.getString("name", "").toString()
            val savedTeam = prefID.getString("team", "").toString()
            val savedPosition = prefID.getString("position", "").toString()
            val savedImg = prefID.getString("img", "").toString()
            val savedNo = prefID.getString("no", "").toString()
            if(savedId.equals("")){
                //id 폴더에 아무런 값도 없다면 아무 일도 일어나지 않음.
            }
            else{//저장된 id의 값이 있다면 해당 id의 값을 Toast에 띄워주고 메인 액티비티로 이동
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("myId", savedId)
                intent.putExtra("myPw", savedPw)
                intent.putExtra("myName", savedName)
                intent.putExtra("myTeam", savedTeam)
                intent.putExtra("PhotoDir", savedImg)
                intent.putExtra("myPosition", savedPosition)
                intent.putExtra("myNo", savedNo)

                startActivity(intent)
                Toast.makeText(this, savedId + "자동 로그인 하였습니다", Toast.LENGTH_SHORT).show()
                finish()
            }

            loginBtn.setOnClickListener {
                //패턴 라이브러리로 컴파일된 패턴을 matcher 함수를 사용하여 모두 포함될때 !false = true 값 반환
                if (!patternID.matcher(loginId.text.toString()).find()) {
                    loginId.setText("")
                    loginId.setHint("영문+숫자 입력")
                    loginId.setHintTextColor(Color.RED)
                }
                //비밀번호가 숫자 1이므로 주석
                /*if (!patternPW.matcher(loginId.text.toString()).find()) {
                    loginPw.setText("")
                    loginPw.setHint("영문+숫자+특수문자 입력")
                    loginPw.setHintTextColor(Color.RED)
                }*/
                //둘 다 모두 일치할 경우 로그인 실행
                if (patternPW.matcher(loginId.text.toString()).find() &&
                    patternPW.matcher(loginId.text.toString()).find()
                ) {

                }
                //암호화 AEC256 CBC 방식
                var time = System.currentTimeMillis() / 1000 // 1678922299
                val id = loginId.text.toString()+"|"+time.toString()
                val pw = loginPw.text.toString()
                val encoID = id.encryptCBC()
                val encoPW = pw.encryptCBC()

                Log.e("인코딩된 ID", encoID)
                Log.e("인코딩된 PW", encoPW)
                Log.e("현재 시간", time.toString())
                


                //자동 로그인 값 저장 Shared Preferences
                fun saveData(autoID : String, autoPW : String,
                             autoName : String,
                             autoTeam : String,
                             autoPosition : String,
                             autoImg : String,
                             autoNo : String){
                    val pref = getSharedPreferences("autoId", MODE_PRIVATE)
                    val edit = pref.edit() // 수정하기
                    edit.putString("id", autoID)// 값 넣기
                    edit.putString("pw", autoPW)
                    edit.putString("name", autoName)
                    edit.putString("team", autoTeam)
                    edit.putString("position", autoPosition)
                    edit.putString("img", autoImg)
                    edit.putString("no", autoNo)
                    edit.apply() // 적용하기
                }
                //요청하는 비밀번호를 json타입으로 넣어주기 위해 data class json으로 생성
                val requestLoginData = RequestLoginData(
                    encoPW
                )
                val call: Call<ResponseLoginData> =     //조합 인코딩된 ID, 인코딩된 PW json타입으로 담아줌
                    ServiceCreator.RetrofitService.postLogin(encoID.trim(),requestLoginData)

                call.enqueue(object : Callback<ResponseLoginData> {
                    override fun onResponse(
                        call: Call<ResponseLoginData>,
                        response: Response<ResponseLoginData>
                    ) {
                        //response 객체 data custName이 null이 아니면 성공
                        if (response.body()?.data?.CustName != null) {//로그인 성공
                            if(autoLogin.isChecked()){//자동 로그인 체크시 shapepreference 통해 id 값 저장
                                saveData(loginId.text.toString(), loginPw.text.toString(),
                                    response.body()?.data?.CustName.toString(),
                                    response.body()?.data?.PartCodeName.toString(),
                                    response.body()?.data?.PositionCodeName.toString(),
                                    response.body()?.data?.PhotoDir.toString(),
                                    response.body()?.data?.CustNo.toString()
                                )
                            }
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("myId", loginId.text.toString())
                            intent.putExtra("myPw", loginPw.text.toString())
                            intent.putExtra("myName", response.body()?.data?.CustName)
                            intent.putExtra("myNo", response.body()?.data?.CustNo)
                            intent.putExtra("myTeam", response.body()?.data?.PartCodeName)
                            intent.putExtra("myPosition",response.body()?.data?.PositionCodeName )
                            intent.putExtra("PhotoDir",response.body()?.data?.PhotoDir )

                            Toast.makeText(
                                this@LoginActivity,
                                "${response.body()?.data?.CustName}님 반갑습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인에 실패했습니다", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    //response.isSuccessful이 false이거나 body()에 값이 없을 경우 에러 처리
                    // => 서버통신 실패
                    override fun onFailure(call: Call<ResponseLoginData>, t: Throwable) {
                        Log.e("NetworkTest", "error: $t")
                    }
                })

            }
    }

    //CBC AES256
    //외부 함수에서 정적으로 호출 하기 위해 companion 사용
    private fun String.encryptCBC(): String{
        //암호화 키 값을 바이트로 담음  (32 바이트)
        val iv = IvParameterSpec(SECRET_IV)
        //암호화 키의 바이트 값을 AES 알고리즘으로 담습니다.
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        //AES 알고리즘을 사용하여 CBC 모드로 암호화를 수행하는 Cipher 객체를 만듭니다.
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        //cipher 객체의 암호화모드를 설정, 비밀키와 초기화 백터를 사용하여 암호화
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        //이전에 초기화한 cipher 객체를 사용하여 입력 문자열 바이트 배열 암호화
        val crypted = cipher.doFinal(this.toByteArray())
        //암호화된 바이트 배열을 base64 문자열로 인코딩
        val encodedByte = Base64.encode(crypted, Base64.DEFAULT)

        return String(encodedByte)
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

