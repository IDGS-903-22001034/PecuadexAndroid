package com.zurie.pecuadexproject.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Model.GeofenceArea
import com.zurie.pecuadexproject.Data.Model.GpsData
import com.zurie.pecuadexproject.mqtt.MqttManager
import com.zurie.pecuadexproject.utils.LocationUtils
import com.zurie.pecuadexproject.utils.NotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mqttManager = MqttManager(application)
    private val notificationUtils = NotificationUtils(application)

    private val _currentGpsData = MutableStateFlow<GpsData?>(null)
    val currentGpsData: StateFlow<GpsData?> = _currentGpsData

    private val _geofenceArea = MutableStateFlow<GeofenceArea?>(null)
    val geofenceArea: StateFlow<GeofenceArea?> = _geofenceArea

    private val _isInsideGeofence = MutableStateFlow(true)
    val isInsideGeofence: StateFlow<Boolean> = _isInsideGeofence

    private val _alertHistory = MutableStateFlow<List<String>>(emptyList())
    val alertHistory: StateFlow<List<String>> = _alertHistory

    val connectionStatus = mqttManager.connectionStatus

    private var lastAlertTime = 0L
    private val alertCooldownMs = 30000L

    init {
        initializeMqtt()
        Log.d("MainViewModel", "ViewModel inicializado")
    }

    private fun initializeMqtt() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Iniciando conexión MQTT...")
            mqttManager.connect()

            mqttManager.gpsDataFlow.collect { gpsData ->
                gpsData?.let {
                    Log.d("MainViewModel", "Datos GPS recibidos: lat=${it.latitude}, lng=${it.longitude}")
                    _currentGpsData.value = it
                    checkGeofenceStatus(it)
                }
            }
        }
    }

    fun setGeofenceCenter(latitude: Double, longitude: Double) {
        val newGeofence = GeofenceArea(
            centerLatitude = latitude,
            centerLongitude = longitude,
            radiusMeters = 5.0f
        )
        _geofenceArea.value = newGeofence
        Log.d("MainViewModel", "Geocerca establecida en: $latitude, $longitude")

        _currentGpsData.value?.let { gpsData ->
            val isInside = LocationUtils.isInsideGeofence(gpsData, newGeofence)
            _isInsideGeofence.value = isInside
            Log.d("MainViewModel", "Estado inicial de geocerca: ${if (isInside) "dentro" else "fuera"}")

        }
    }

    private fun checkGeofenceStatus(gpsData: GpsData) {
        val geofence = _geofenceArea.value
        if (geofence == null) {
            Log.d("MainViewModel", "No hay geocerca configurada")
            return
        }

        val isInside = LocationUtils.isInsideGeofence(gpsData, geofence)
        val wasInside = _isInsideGeofence.value

        Log.d("MainViewModel", "Verificando geocerca: isInside=$isInside, wasInside=$wasInside")

        _isInsideGeofence.value = isInside

        if (wasInside && !isInside) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAlertTime > alertCooldownMs) {
                Log.d("MainViewModel", "¡Animal salió de la cerca! Enviando alerta...")
                sendAlert()
                lastAlertTime = currentTime
            } else {
                Log.d("MainViewModel", "Alerta en cooldown, no enviando")
            }
        } else if (!wasInside && isInside) {
            Log.d("MainViewModel", "Animal regresó a la cerca")
        }
    }

    private fun sendAlert() {
        Log.d("MainViewModel", "=== ENVIANDO ALERTA ===")

        try {
            notificationUtils.sendGeofenceAlert()
            Log.d("MainViewModel", "✅ Notificación enviada")
        } catch (e: Exception) {
            Log.e("MainViewModel", "❌ Error al enviar notificación", e)
        }

        try {
            val timestamp = java.text.SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date())

            val newAlert = "Animal salió de la cerca - $timestamp"
            Log.d("MainViewModel", "Creando alerta: $newAlert")

            val currentAlerts = _alertHistory.value.toMutableList()
            Log.d("MainViewModel", "Alertas actuales: ${currentAlerts.size}")

            currentAlerts.add(newAlert)
            Log.d("MainViewModel", "Alertas después de agregar: ${currentAlerts.size}")

            _alertHistory.value = currentAlerts
            Log.d("MainViewModel", "✅ Alerta agregada al historial")

            Log.d("MainViewModel", "Estado final del historial: ${_alertHistory.value.size} alertas")
            _alertHistory.value.forEach { alert ->
                Log.d("MainViewModel", "  - $alert")
            }

        } catch (e: Exception) {
            Log.e("MainViewModel", "❌ Error al agregar alerta al historial", e)
        }

        Log.d("MainViewModel", "=== FIN ENVÍO ALERTA ===")
    }

    fun clearAlertHistory() {
        _alertHistory.value = emptyList()
        Log.d("MainViewModel", "Historial de alertas limpiado")
    }

    fun testAlert() {
        Log.d("MainViewModel", "=== ALERTA DE PRUEBA INICIADA ===")
        Log.d("MainViewModel", "Historial actual antes de prueba: ${_alertHistory.value.size} alertas")
        sendAlert()
        Log.d("MainViewModel", "Historial después de prueba: ${_alertHistory.value.size} alertas")
        Log.d("MainViewModel", "=== ALERTA DE PRUEBA COMPLETADA ===")
    }

    override fun onCleared() {
        super.onCleared()
        mqttManager.disconnect()
        Log.d("MainViewModel", "ViewModel destruido")
    }
}