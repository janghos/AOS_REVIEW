package com.jangho.rad_app.Model

data class ResponseLoginData(
    val responseCode: String,
    val responseMsg: String,
    val data: Data
    )
{
    data class Data(
        val CustName: String,
        val CustNo : String,
        val PartCodeName : String,
        val PositionCodeName : String,
        val PhotoDir : String,
        val PhotoUrl : String,
    )
}
