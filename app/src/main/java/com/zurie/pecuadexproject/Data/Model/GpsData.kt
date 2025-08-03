package com.zurie.pecuadexproject.Data.Model

import com.google.gson.annotations.SerializedName

data class GpsData(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("altitude")
    val altitude: Double,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("satellites")
    val satellites: Int,
    @SerializedName("hdop")
    val hdop: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String
)
