package com.jangho.rad_app.Model

import com.google.gson.annotations.SerializedName

data class RequestLoginData(
    @SerializedName("pwd") //pwd 키 값 GSON , Java에서 Json을 파싱하고, 생성하기 위해 사용되는 구글개발오픈소스
    //json타입으로 변환하여 키 값으로 보냄
    val pwd : String
)
