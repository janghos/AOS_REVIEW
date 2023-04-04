package com.jangho.rad_app.GPSPage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jangho.rad_app.databinding.ActivityGpsBinding
import com.minew.beaconset.BluetoothState
import com.minew.beaconset.MinewBeacon
import com.minew.beaconset.MinewBeaconManager
import com.minew.beaconset.MinewBeaconManagerListener
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.jangho.rad_app.Model.LatLngEntity
import com.jangho.rad_app.Model.ResponseBeacon
import com.jangho.rad_app.MyPage.MyPageActivity
import com.jangho.rad_app.R
import com.jangho.rad_app.Service.ServiceCreator
import com.jangho.rad_app.SettingPage.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GpsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityGpsBinding

    private lateinit var mMinewBeaconManager: MinewBeaconManager
    private lateinit var beaconList :  MutableList<String>

    //현재 위치 가져오는 함수
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var getLongitude : String
    private lateinit var getLatitude : String

    //지도 관련
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? =  null


    var mBackWait:Long = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkVerify()
        blueCheck()
        binding = ActivityGpsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        beaconSearch()
        //api 정보 받고 비콘 실행 안그러면 재접속시 에러 발생
        // beaconList 초기화 X


        Log.e("dd",intent.getStringExtra("myNo").toString())
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (checkPermissionForLocation(this)) {
            startLocationUpdates()
            this.mapView = binding.mapView
            mapView.onCreate(savedInstanceState)
        }
        else{
            Toast.makeText(this, "위치  권한을 허용 이후 해당 페이지 접근 가능합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.closeBtn.setOnClickListener {
            mMinewBeaconManager.stopScan()
            mMinewBeaconManager.stopService()
            finish()
        }
        //블루투스 권한 없을시
        if (baseContext.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ) {
            mMinewBeaconManager = MinewBeaconManager.getInstance(this)
            mMinewBeaconManager.startService()
            startBeacon()
            true
        } else {
            Toast.makeText(this, "블루투스 권한을 허용 이후 해당 페이지 접근 가능합니다.", Toast.LENGTH_SHORT).show()
            finish()
            // 권한이 없으므로 권한 요청 알림 보내기
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,)
                ,1000)
            binding.loadingBea.setText("블루투스 권한이 없음")
        }
    }

    //Map이 사용할 준비가 되었을 때 호출
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        currentMarker = setupMarker(LatLngEntity(getLatitude.toDouble(),getLongitude.toDouble()))  // default 마지막 위치
        currentMarker?.showInfoWindow()
    }

    //선택한 위치의 marker 표시
    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            Log.e("positionLatLng",positionLatLng.toString())
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // 지도 유형 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))  // 카메라 이동
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))  // 줌의 정도 - 1 일 경우 세계지도 수준, 숫자가 커질 수록 상세지도가 표시됨
        return googleMap.addMarker(markerOption)

    }

    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        getLatitude = mLastLocation.latitude.toString() // 갱신 된 위도
        getLongitude = mLastLocation.longitude.toString() // 갱신 된 경도
        mapView.getMapAsync(this@GpsActivity)//장소가 바뀌면 업데이트가 됨
        binding.loadingGps.setText("지도 로딩이 완료되었습니다!")

    }

    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    fun checkVerify() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED )
             {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    "권한 관련 요청을 허용해 주셔야 이용 가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // 카메라 및 저장공간 권한 요청
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                    ), 1
                )
            }
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()//맵뷰를 start와 resume에 실행
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()

    }
    override fun onStop() {
        super.onStop()

    }
    override fun onLowMemory() {
        super.onLowMemory()
    }
    override fun onDestroy() {

        super.onDestroy()
    }


    private fun startBeacon() {
        mMinewBeaconManager.startScan()
        mMinewBeaconManager.setMinewbeaconManagerListener(object : MinewBeaconManagerListener {
            override fun onUpdateBluetoothState(p0: BluetoothState?) {
            }

            // 비콘 상태 지속적으로 갱신
            override fun onRangeBeacons(p0: MutableList<MinewBeacon>?) {
                if (!p0.isNullOrEmpty()) {
                    for (i in p0) {
                        if (::beaconList.isInitialized) {
                            for (j in beaconList) {
                                if (i.uuid.equals(j, true)) {
                                    if (binding.title1.text == "") {
                                        binding.title1.setText(i.name)
                                        binding.major1.setText(i.major)
                                        binding.minor1.setText(i.minor)
                                    } else if (binding.title2.text == "" && binding.title1.text != i.name) {
                                        binding.title2.setText(i.name)
                                        binding.major2.setText(i.major)
                                        binding.minor2.setText(i.minor)
                                    } else if (binding.title3.text == "" && binding.title2.text != i.name && binding.title1.text != i.name) {
                                        binding.title3.setText(i.name)
                                        binding.major3.setText(i.major)
                                        binding.minor3.setText(i.minor)
                                    }
                                }
                            }
                            binding.loadingBea.setText("비콘 로딩 완료")
                        } else {

                        }
                    }
                }
            }
            override fun onAppearBeacons(p0: MutableList<MinewBeacon>?) {
            }

            override fun onDisappearBeacons(p0: MutableList<MinewBeacon>?) {
            }
        })
    }

    private fun beaconSearch() {
        //받을 매개변수 값인 ResponseBeacon 데이터 클래스 타입을 선언함
        val call: Call<ResponseBeacon> =
            ServiceCreator.RetrofitService.getBeacon()

        call.enqueue(object : Callback<ResponseBeacon> {
            override fun onResponse(
                call: Call<ResponseBeacon>,
                response: Response<ResponseBeacon>
            ) { //respose 응답값 성공시,
                if (response.isSuccessful) {
                    val list = mutableListOf<String>()
                    val data = response.body()?.data
                    if(!data.isNullOrEmpty()) {
                        for(i in data){
                            val beaconData = i.BeaId
                            list.add(beaconData)
                        }
                        beaconList = list
                    }
                }
            }
            //response.isSuccessful이 false이거나 body()에 값이 없을 경우 에러 처리
            // => 서버통신 실패
            override fun onFailure(call: Call<ResponseBeacon>, t: Throwable) {
                Toast.makeText(baseContext,"비콘 정보 받아오기 실패입니다! 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                Log.e("dd",t.toString())
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun blueCheck(){
        //블루투스 권한 없을시
        if (baseContext.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ) {
            true
        } else {
            // 권한이 없으므로 권한 요청 알림 보내기
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,)
                ,1000)
            false
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
}




