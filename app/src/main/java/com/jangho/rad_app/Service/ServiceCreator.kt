package com.jangho.rad_app.Service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {	//서비스를 생성해주는 구현체 부분
    private const val BASE_URL = "https://dev-api.radcns.com:452"

    //Retrofit 객체 생성
    private val retrofit: Retrofit = Retrofit
        //레트로핏 빌더 생성 (생성자 호출)
        .Builder()
        //빌더 객체의 baseUrl 호출. 서버의 메인 URL 전달
        .baseUrl(BASE_URL)
        //gson 컨버터 연동 // 입력시 gson으로 데이터 값 request하기 위해서 컨버터 실행
            //그래서 RequestData 또한 datamodel에 SerializedName어노테이션 통해 담음
        .addConverterFactory(GsonConverterFactory.create())
        //Retrofit 객체 반환
        .build()

    //인터페이스 객체를 create에 넘겨 실제 구현체 생성
    val RetrofitService : RetrofitService = retrofit.create(com.jangho.rad_app.Service.RetrofitService::class.java)
}