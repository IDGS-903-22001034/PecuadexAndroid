package com.zurie.pecuadexproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationUtils(private val context: Context) {

    private val channelId = "geofence_alerts"
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Geocerca",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando el animal sale de la cerca"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationUtils", "Canal de notificación creado")
        }
    }

    fun sendGeofenceAlert(animalName: String = "Animal") {
        Log.d("NotificationUtils", "Creando notificación...")

        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Icono temporal
                .setContentTitle("⚠️ Alerta de Geocerca")
                .setContentText("$animalName ha salido del área segura")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .build()

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)

            Log.d("NotificationUtils", "Notificación enviada con ID: $notificationId")
        } catch (e: Exception) {
            Log.e("NotificationUtils", "Error al enviar notificación", e)
        }
    }
}