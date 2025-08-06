package com.zurie.pecuadexproject.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Model.GeofenceArea
import com.zurie.pecuadexproject.Data.Model.GpsData
import com.zurie.pecuadexproject.mqtt.MqttManager
import com.zurie.pecuadexproject.utils.AnimalSimulator
import com.zurie.pecuadexproject.utils.LocationUtils
import com.zurie.pecuadexproject.utils.NotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mqttManager = MqttManager(application)
    private val notificationUtils = NotificationUtils(application)
    private val animalSimulator = AnimalSimulator()

    // Datos del animal real (tu dispositivo GPS)
    private val _currentGpsData = MutableStateFlow<GpsData?>(null)
    val currentGpsData: StateFlow<GpsData?> = _currentGpsData

    // Todos los animales (real + simulados)
    private val _allAnimalsData = MutableStateFlow<List<GpsData>>(emptyList())
    val allAnimalsData: StateFlow<List<GpsData>> = _allAnimalsData

    private val _geofenceArea = MutableStateFlow<GeofenceArea?>(null)
    val geofenceArea: StateFlow<GeofenceArea?> = _geofenceArea

    private val _isInsideGeofence = MutableStateFlow(true)
    val isInsideGeofence: StateFlow<Boolean> = _isInsideGeofence

    private val _alertHistory = MutableStateFlow<List<String>>(emptyList())
    val alertHistory: StateFlow<List<String>> = _alertHistory

    // Estado de simulación
    private val _isSimulationActive = MutableStateFlow(false)
    val isSimulationActive: StateFlow<Boolean> = _isSimulationActive

    val connectionStatus = mqttManager.connectionStatus

    private var lastAlertTime = 0L
    private val alertCooldownMs = 30000L

    init {
        initializeMqtt()
        setupSimulator()
        Log.d("MainViewModel", "ViewModel inicializado con simulador")
    }

    private fun initializeMqtt() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Iniciando conexión MQTT...")
            mqttManager.connect()

            mqttManager.gpsDataFlow.collect { gpsData ->
                gpsData?.let {
                    Log.d("MainViewModel", "Datos GPS REALES recibidos: lat=${it.latitude}, lng=${it.longitude}")
                    _currentGpsData.value = it

                    // Actualizar el simulador con el animal real
                    animalSimulator.updateRealAnimal(it)

                    checkGeofenceStatus(it)
                }
            }
        }
    }

    private fun setupSimulator() {
        viewModelScope.launch {
            animalSimulator.allGpsData.collect { allAnimals ->
                _allAnimalsData.value = allAnimals
                Log.d("MainViewModel", "Datos de simulador actualizados: ${allAnimals.size} animales")
            }
        }
    }

    // Función para iniciar/detener la simulación
    fun toggleSimulation() {
        if (_isSimulationActive.value) {
            stopSimulation()
        } else {
            startSimulation()
        }
    }

    fun startSimulation() {
        Log.d("MainViewModel", "Iniciando simulación de animales")
        animalSimulator.startSimulation(_currentGpsData.value)
        _isSimulationActive.value = true
    }

    fun stopSimulation() {
        Log.d("MainViewModel", "Deteniendo simulación de animales")
        animalSimulator.stopSimulation()
        _isSimulationActive.value = false

        // Volver a mostrar solo el animal real si existe
        _currentGpsData.value?.let { realAnimal ->
            _allAnimalsData.value = listOf(realAnimal)
        } ?: run {
            _allAnimalsData.value = emptyList()
        }
    }

    // Función actualizada para crear geocerca con radio personalizable
    fun setGeofenceCenter(latitude: Double, longitude: Double, radiusMeters: Float = 5.0f) {
        val newGeofence = GeofenceArea(
            centerLatitude = latitude,
            centerLongitude = longitude,
            radiusMeters = radiusMeters,
            isActive = true
        )
        _geofenceArea.value = newGeofence
        Log.d("MainViewModel", "Geocerca establecida en: $latitude, $longitude con radio: ${radiusMeters}m")

        // Verificar inmediatamente el estado si hay datos GPS del animal real
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

        // Solo verificar geocerca para el animal REAL, no los simulados
        // ✅ Tu ESP32 se identifica como "ESP32_GPS_Client"
        if (gpsData.deviceId != "REAL_DEVICE" &&
            !gpsData.deviceId.contains("ESP32") && // ← Detecta tu ESP32
            gpsData.deviceId != "ESP32_GPS_Client") { // ← Específico para tu device
            return // Ignorar animales simulados para alertas
        }

        val isInside = LocationUtils.isInsideGeofence(gpsData, geofence)
        val wasInside = _isInsideGeofence.value

        Log.d("MainViewModel", "Verificando geocerca para animal REAL: isInside=$isInside, wasInside=$wasInside")

        _isInsideGeofence.value = isInside

        if (wasInside && !isInside) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAlertTime > alertCooldownMs) {
                Log.d("MainViewModel", "¡Animal REAL salió de la cerca! Enviando alerta...")
                sendAlert()
                lastAlertTime = currentTime
            } else {
                Log.d("MainViewModel", "Alerta en cooldown, no enviando")
            }
        } else if (!wasInside && isInside) {
            Log.d("MainViewModel", "Animal REAL regresó a la cerca")
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

            val newAlert = "Animal real salió de la cerca - $timestamp"
            Log.d("MainViewModel", "Creando alerta: $newAlert")

            val currentAlerts = _alertHistory.value.toMutableList()
            currentAlerts.add(newAlert)
            _alertHistory.value = currentAlerts

            Log.d("MainViewModel", "✅ Alerta agregada al historial")

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
        sendAlert()
        Log.d("MainViewModel", "=== ALERTA DE PRUEBA COMPLETADA ===")
    }

    // Función para obtener estadísticas del rebaño simulado
    fun getFlockStats(): FlockStats {
        val allAnimals = _allAnimalsData.value
        val realAnimal = allAnimals.firstOrNull { it.deviceId == "REAL_DEVICE" || it.deviceId.contains("ESP") }
        val simulatedAnimals = allAnimals.filter { !it.deviceId.contains("REAL") && !it.deviceId.contains("ESP") }

        return FlockStats(
            totalAnimals = allAnimals.size,
            realAnimals = if (realAnimal != null) 1 else 0,
            simulatedAnimals = simulatedAnimals.size,
            cows = simulatedAnimals.count { it.deviceId.contains("COW") },
            bulls = simulatedAnimals.count { it.deviceId.contains("BULL") },
            calves = simulatedAnimals.count { it.deviceId.contains("CALF") },
            sheep = simulatedAnimals.count { it.deviceId.contains("SHEEP") },
            goats = simulatedAnimals.count { it.deviceId.contains("GOAT") }
        )
    }

    override fun onCleared() {
        super.onCleared()
        animalSimulator.cleanup()
        mqttManager.disconnect()
        Log.d("MainViewModel", "ViewModel destruido")
    }
}

data class FlockStats(
    val totalAnimals: Int,
    val realAnimals: Int,
    val simulatedAnimals: Int,
    val cows: Int,
    val bulls: Int,
    val calves: Int,
    val sheep: Int,
    val goats: Int
)