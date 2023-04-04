package com.jangho.rad_app.Model

data class ResponseVersion(
    val responseCode: String,
    val responseMsg: String,
    val data: Data
)
{
    data class Data(
        val AOSMinVersion: String
    )
}