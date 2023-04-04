package com.jangho.rad_app.Model

data class ResponseBeacon(
    val responseCode: String,
    val responseMsg: String,
    val data: MutableList<Data>
)
{
    data class Data(
        val CodeName: String,
        val BeaId : String,
        val RegionCode : String,
        val CommuteTime : String,
        val Address : String,
    )
}

