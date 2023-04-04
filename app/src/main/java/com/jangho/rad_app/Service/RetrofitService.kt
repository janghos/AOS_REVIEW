package com.jangho.rad_app.Service

import com.google.gson.JsonElement
import com.jangho.rad_app.Model.RequestLoginData
import com.jangho.rad_app.Model.ResponseBeacon
import com.jangho.rad_app.Model.ResponseLoginData
import com.jangho.rad_app.Model.ResponseVersion
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RetrofitService {
    //@Headers 어노테이션 이용해 헤더값 넣어주기
    @Headers("Content-Type: application/json")
    //HTTP 메소드를 설정해주고 API와 URL 작성

    @POST("Api/Employee/LogOn")
    fun postLogin(
        @Header("Authorization") authorization : String,
        //@Body 어노테이션을 통해 RequestBody 데이터를 넣어준다.
        @Body requestLoginData: RequestLoginData) : Call<ResponseLoginData>

    @GET("Api/Employee/variousData")
    fun getVersion(@Query("dataCode") dataCode : String): Call<ResponseVersion> //json타입으로 가져옴

    @POST("Api/Employee/BeaconInfoS")
    fun getBeacon() : Call<ResponseBeacon>
}