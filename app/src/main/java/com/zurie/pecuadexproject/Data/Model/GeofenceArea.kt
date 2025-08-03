package com.zurie.pecuadexproject.Data.Model

data class GeofenceArea(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Float = 5.0f,
    val isActive: Boolean = true
)
