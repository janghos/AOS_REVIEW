package com.jangho.rad_app

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ReportActivity : AppCompatActivity() {

    var filePathCallbackValue: ValueCallback<Array<Uri>>? = null
    val FILECHOOSER_REQ_CODE = 100
    private var cameraImageUri: Uri? = null
    var mBackWait: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val web = findViewById<WebView>(R.id.report_view)
        val close = findViewById<Button>(R.id.closeBtn)

        //기본적으로 자바스크립트는 WebView에서 사용 중지됩니다.
        // 자바스크립트는 WebView에 연결된 WebSettings를 통해 사용 설정할 수 있습니다.
        //getSettings()로 WebSettings를 가져온 다음 setJavaScriptEnabled()로 자바스크립트를 사용 설정할 수 있습니다.
        web.settings.javaScriptEnabled = true
        web.settings.setSupportMultipleWindows(false)//새창 띄우기
        web.settings.builtInZoomControls = true // 화면 확대 축소 허용 여부
        web.settings.loadWithOverviewMode = true //컨텐츠가 웹뷰보다 클경우 스크린 크기에 맞춰 조정
        web.settings.defaultTextEncodingName = "UTF-8"

        web.webChromeClient = object : WebChromeClient(){
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                // Callback 초기화 (중요!)
                if (filePathCallbackValue != null) {
                    filePathCallbackValue!!.onReceiveValue(null)
                    filePathCallbackValue = null
                }
                filePathCallbackValue = filePathCallback

                if (baseContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED  &&
                    baseContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    val isCapture = fileChooserParams.isCaptureEnabled
                    runCamera(isCapture)
                } else {
                    // 권한이 없으므로 권한 요청 알림 보내기
                    ActivityCompat.requestPermissions(
                        this@ReportActivity, arrayOf(
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1
                    )
                }
                return true
            }
        }
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        // 웹뷰 로드
        web.loadUrl("http://m.elandethic.co.kr/report/reportRegister")

        // 닫기 버튼 클릭 처리
        close.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 한번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)//액티비티 종료
            System.exit(0); //프로세스 종료
        }
    }
    private fun runCamera(_isCapture: Boolean) {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.getAbsolutePath()
        val file = File(path, "$timeStamp.png") // sample.png 는 카메라로 찍었을 때 저장될 파일명이므로 사용자 마음대로
        // File 객체의 URI 를 얻는다.
        cameraImageUri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val strpa = applicationContext.packageName
            FileProvider.getUriForFile(this, "$strpa.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        if (!_isCapture) { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "사진 가져올 방법을 선택하세요."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                arrayOf<Parcelable>(intentCamera)
            )
            startActivityForResult(chooserIntent, FILECHOOSER_REQ_CODE)
        } else { // 바로 카메라 실행..
            startActivityForResult(intentCamera, FILECHOOSER_REQ_CODE)
        }
    }
    //권한 획득 여부에 따른 결과 반환
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            //퍼미션 권한 요청 이후 파일 첨부 버튼 활성화
            if (filePathCallbackValue != null) {
                filePathCallbackValue!!.onReceiveValue(null)
                filePathCallbackValue = null
            }
            //권한 거부 혹 반복적으로 거부하여 권한이 없을 경우 애플리케이션 정보로 이동
            if (grantResults.size > 0) {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", this.packageName, null)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // FLAG_ACTIVITY_NEW_TASK 추가
                        startActivity(intent)
                        Toast.makeText(
                            applicationContext,
                            "권한 관련 요청 허용해야 카메라 캡처이미지 사용등의 서비스 정상 이용",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    else{

                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var data = data
        Log.d("onActivityResult() ", "resultCode = " + Integer.toString(requestCode))
        when (requestCode) {
            FILECHOOSER_REQ_CODE -> {
                if (resultCode == RESULT_OK) {
                    if (filePathCallbackValue == null) return
                    if (data == null) data = Intent()
                    if (data.data == null) data.data = cameraImageUri //파일 사이즈 갖고 와서 파일 첨부를 확인
                    filePathCallbackValue!!.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                }
                filePathCallbackValue = null
            }
            else -> {}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}




