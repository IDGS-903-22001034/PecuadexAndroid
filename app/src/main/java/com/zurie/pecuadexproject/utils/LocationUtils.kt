package com.zurie.pecuadexproject.utils

import android.location.Location
import android.util.Log
import com.zurie.pecuadexproject.Data.Model.GeofenceArea
import com.zurie.pecuadexproject.Data.Model.GpsData

object LocationUtils {
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun isInsideGeofence(gpsData: GpsData, geofenceArea: GeofenceArea): Boolean {
        val distance = calculateDistance(
            gpsData.latitude, gpsData.longitude,
            geofenceArea.centerLatitude, geofenceArea.centerLongitude
        )

        val isInside = distance <= geofenceArea.radiusMeters

        Log.d("LocationUtils",
            "Distancia: ${String.format("%.2f", distance)}m, " +
                    "Radio: ${geofenceArea.radiusMeters}m, " +
                    "Dentro: $isInside"
        )

        return isInside
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        return "Lat: ${String.format("%.6f", latitude)}, " +
                "Lng: ${String.format("%.6f", longitude)}"
    }
}