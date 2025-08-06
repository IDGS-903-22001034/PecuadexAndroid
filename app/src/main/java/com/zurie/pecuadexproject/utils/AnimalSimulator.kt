package com.zurie.pecuadexproject.utils

import android.util.Log
import com.zurie.pecuadexproject.Data.Model.GpsData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*
import kotlin.random.Random

data class SimulatedAnimal(
    val id: String,
    val name: String,
    val type: AnimalType,
    var latitude: Double,
    var longitude: Double,
    var speed: Double, // km/h
    var direction: Double, // Dirección en grados (0-360)
    var lastUpdate: Long,
    var isMoving: Boolean = true,
    var restTime: Long = 0L, // Tiempo que lleva descansando
    var targetLat: Double? = null, // Coordenadas objetivo para movimiento dirigido
    var targetLng: Double? = null,
    var behavior: AnimalBehavior = AnimalBehavior.GRAZING
)

enum class AnimalType {
    COW, BULL, CALF, SHEEP, GOAT
}

enum class AnimalBehavior {
    GRAZING,    // Pastando - movimiento lento y aleatorio
    RESTING,    // Descansando - sin movimiento
    DRINKING,   // Bebiendo - moviendo hacia fuente de agua
    FOLLOWING,  // Siguiendo a otro animal
    WANDERING   // Vagando - movimiento aleatorio
}

class AnimalSimulator {
    private val _simulatedAnimals = MutableStateFlow<List<SimulatedAnimal>>(emptyList())
    val simulatedAnimals: StateFlow<List<SimulatedAnimal>> = _simulatedAnimals

    private val _allGpsData = MutableStateFlow<List<GpsData>>(emptyList())
    val allGpsData: StateFlow<List<GpsData>> = _allGpsData

    private var simulationJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Configuración del simulador
    private val UPDATE_INTERVAL_MS = 2000L // Actualizar cada 2 segundos
    private val MAX_SPEED_KMH = 8.0 // Velocidad máxima realista para ganado
    private val MIN_SPEED_KMH = 0.5 // Velocidad mínima
    private val REST_PROBABILITY = 0.15 // 15% probabilidad de descansar
    private val DIRECTION_CHANGE_PROBABILITY = 0.3 // 30% probabilidad de cambiar dirección

    // Coordenadas del área de simulación (puedes ajustar estas)
    private val SIMULATION_CENTER_LAT = 21.063562939245507
    private val SIMULATION_CENTER_LNG = -101.58053658565431
    private val SIMULATION_RADIUS_KM = 0.2 // Radio de 200m para el área (más pequeño)

    fun startSimulation(realAnimal: GpsData? = null) {
        Log.d("AnimalSimulator", "Iniciando simulación con ${if (realAnimal != null) "animal real" else "solo simulados"}")

        if (simulationJob?.isActive == true) {
            Log.d("AnimalSimulator", "Simulación ya está corriendo")
            return
        }

        // Crear animales simulados
        createSimulatedAnimals(realAnimal)

        simulationJob = coroutineScope.launch {
            while (isActive) {
                updateAllAnimals()
                convertToGpsData(realAnimal)
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    fun stopSimulation() {
        Log.d("AnimalSimulator", "Deteniendo simulación")
        simulationJob?.cancel()
        _simulatedAnimals.value = emptyList()
        _allGpsData.value = emptyList()
    }

    fun updateRealAnimal(realGpsData: GpsData) {
        // Actualizar la lista incluyendo el animal real
        convertToGpsData(realGpsData)
    }

    private fun createSimulatedAnimals(realAnimal: GpsData?) {
        val animals = mutableListOf<SimulatedAnimal>()

        // Crear diferentes tipos de animales
        val animalConfigs = listOf(
            AnimalType.COW to 20,
        )

        var animalId = 1
        animalConfigs.forEach { (type, count) ->
            repeat(count) {
                val basePos = if (realAnimal != null) {
                    // Crear animales cerca del animal real
                    Pair(realAnimal.latitude, realAnimal.longitude)
                } else {
                    // Crear animales en el centro de simulación
                    Pair(SIMULATION_CENTER_LAT, SIMULATION_CENTER_LNG)
                }

                // Generar posición aleatoria cerca de la base
                val distance = Random.nextDouble(0.0005, 0.002) // Entre 50m y 200m (más cerca)
                val angle = Random.nextDouble(0.0, 2 * PI)

                val lat = basePos.first + (distance * cos(angle))
                val lng = basePos.second + (distance * sin(angle))

                animals.add(
                    SimulatedAnimal(
                        id = "SIM_${type.name}_${String.format("%02d", animalId++)}",
                        name = generateAnimalName(type, it + 1),
                        type = type,
                        latitude = lat,
                        longitude = lng,
                        speed = getRandomSpeed(type),
                        direction = Random.nextDouble(0.0, 360.0),
                        lastUpdate = System.currentTimeMillis(),
                        behavior = getRandomBehavior(type)
                    )
                )
            }
        }

        _simulatedAnimals.value = animals
        Log.d("AnimalSimulator", "Creados ${animals.size} animales simulados")
    }

    private fun updateAllAnimals() {
        val currentTime = System.currentTimeMillis()
        val updatedAnimals = _simulatedAnimals.value.map { animal ->
            updateAnimalPosition(animal, currentTime)
        }
        _simulatedAnimals.value = updatedAnimals
    }

    private fun updateAnimalPosition(animal: SimulatedAnimal, currentTime: Long): SimulatedAnimal {
        val timeDelta = (currentTime - animal.lastUpdate) / 1000.0 // segundos

        // Actualizar comportamiento ocasionalmente
        val newBehavior = if (Random.nextDouble() < 0.1) { // 10% probabilidad
            getRandomBehavior(animal.type)
        } else {
            animal.behavior
        }

        // Determinar si el animal se mueve
        val shouldMove = when (newBehavior) {
            AnimalBehavior.RESTING -> {
                if (animal.restTime > 30000) { // Después de 30s descansando, volver a moverse
                    false
                } else {
                    Random.nextDouble() > REST_PROBABILITY
                }
            }
            else -> Random.nextDouble() > REST_PROBABILITY
        }

        if (!shouldMove) {
            return animal.copy(
                behavior = AnimalBehavior.RESTING,
                isMoving = false,
                restTime = animal.restTime + (currentTime - animal.lastUpdate),
                lastUpdate = currentTime
            )
        }

        // Cambiar dirección ocasionalmente
        val newDirection = if (Random.nextDouble() < DIRECTION_CHANGE_PROBABILITY) {
            // Cambio suave de dirección (±45 grados)
            val directionChange = Random.nextDouble(-45.0, 45.0)
            (animal.direction + directionChange) % 360.0
        } else {
            animal.direction
        }

        // Actualizar velocidad según el tipo y comportamiento
        val newSpeed = getSpeedForBehavior(animal.type, newBehavior)

        // Calcular nueva posición
        val distanceKm = (newSpeed * timeDelta) / 3600.0 // Convertir a km
        val newLat = animal.latitude + (distanceKm * cos(Math.toRadians(newDirection)) / 111.32)
        val newLng = animal.longitude + (distanceKm * sin(Math.toRadians(newDirection)) / (111.32 * cos(Math.toRadians(animal.latitude))))

        // Mantener animales dentro del área de simulación
        val constrainedPosition = constrainToSimulationArea(newLat, newLng)

        return animal.copy(
            latitude = constrainedPosition.first,
            longitude = constrainedPosition.second,
            speed = newSpeed,
            direction = newDirection,
            behavior = newBehavior,
            isMoving = true,
            restTime = 0L,
            lastUpdate = currentTime
        )
    }

    private fun constrainToSimulationArea(lat: Double, lng: Double): Pair<Double, Double> {
        val distance = LocationUtils.calculateDistance(
            lat, lng,
            SIMULATION_CENTER_LAT, SIMULATION_CENTER_LNG
        ) / 1000.0 // Convertir a km

        return if (distance <= SIMULATION_RADIUS_KM) {
            Pair(lat, lng)
        } else {
            // Si está fuera del área, mover hacia el centro
            val angle = atan2(lng - SIMULATION_CENTER_LNG, lat - SIMULATION_CENTER_LAT)
            val maxDistance = SIMULATION_RADIUS_KM * 0.9 // 90% del radio máximo

            val newLat = SIMULATION_CENTER_LAT + (maxDistance * cos(angle) * 111.32)
            val newLng = SIMULATION_CENTER_LNG + (maxDistance * sin(angle) * 111.32 * cos(Math.toRadians(SIMULATION_CENTER_LAT)))

            Pair(newLat, newLng)
        }
    }

    private fun convertToGpsData(realAnimal: GpsData?) {
        val allData = mutableListOf<GpsData>()

        // Añadir animal real si existe
        realAnimal?.let {
            allData.add(it.copy(deviceId = "REAL_DEVICE"))
        }

        // Convertir animales simulados a GpsData
        _simulatedAnimals.value.forEach { animal ->
            val gpsData = GpsData(
                deviceId = animal.id,
                timestamp = animal.lastUpdate,
                latitude = animal.latitude,
                longitude = animal.longitude,
                altitude = Random.nextDouble(1500.0, 1600.0), // Altitud simulada
                speed = animal.speed,
                satellites = Random.nextInt(4, 12), // Satélites simulados
                hdop = Random.nextDouble(0.8, 2.5), // Precisión simulada
                date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(animal.lastUpdate)),
                time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(animal.lastUpdate))
            )
            allData.add(gpsData)
        }

        _allGpsData.value = allData
        Log.d("AnimalSimulator", "Datos GPS actualizados: ${allData.size} animales")
    }

    private fun generateAnimalName(type: AnimalType, number: Int): String {
        val names = when (type) {
            AnimalType.COW -> listOf("Lola", "Marta", "Rosa", "Carmen", "Elena", "Ana", "Sofia", "Luna")
            AnimalType.BULL -> listOf("Torito", "Bravo", "Fuerte", "Rey")
            AnimalType.CALF -> listOf("Pequeño", "Bebé", "Junior", "Mini", "Chico")
            AnimalType.SHEEP -> listOf("Blanca", "Lanita", "Nube", "Copito", "Dulce", "Suave")
            AnimalType.GOAT -> listOf("Cabrita", "Saltarina", "Ágil")
        }
        return names.getOrNull(number - 1) ?: "${type.name} $number"
    }

    private fun getRandomSpeed(type: AnimalType): Double {
        return when (type) {
            AnimalType.COW -> Random.nextDouble(0.5, 4.0)
            AnimalType.BULL -> Random.nextDouble(1.0, 6.0)
            AnimalType.CALF -> Random.nextDouble(1.0, 5.0)
            AnimalType.SHEEP -> Random.nextDouble(0.8, 4.5)
            AnimalType.GOAT -> Random.nextDouble(1.2, 6.0)
        }
    }

    private fun getSpeedForBehavior(type: AnimalType, behavior: AnimalBehavior): Double {
        val baseSpeed = getRandomSpeed(type)
        return when (behavior) {
            AnimalBehavior.RESTING -> 0.0
            AnimalBehavior.GRAZING -> baseSpeed * 0.3
            AnimalBehavior.DRINKING -> baseSpeed * 0.8
            AnimalBehavior.FOLLOWING -> baseSpeed * 1.2
            AnimalBehavior.WANDERING -> baseSpeed
        }
    }

    private fun getRandomBehavior(type: AnimalType): AnimalBehavior {
        val behaviors = when (type) {
            AnimalType.COW -> listOf(
                AnimalBehavior.GRAZING to 0.5,
                AnimalBehavior.RESTING to 0.2,
                AnimalBehavior.DRINKING to 0.1,
                AnimalBehavior.WANDERING to 0.2
            )
            AnimalType.BULL -> listOf(
                AnimalBehavior.GRAZING to 0.3,
                AnimalBehavior.WANDERING to 0.4,
                AnimalBehavior.RESTING to 0.3
            )
            AnimalType.CALF -> listOf(
                AnimalBehavior.FOLLOWING to 0.4,
                AnimalBehavior.GRAZING to 0.3,
                AnimalBehavior.WANDERING to 0.3
            )
            else -> listOf(
                AnimalBehavior.GRAZING to 0.4,
                AnimalBehavior.WANDERING to 0.3,
                AnimalBehavior.RESTING to 0.3
            )
        }

        val random = Random.nextDouble()
        var cumulative = 0.0

        for ((behavior, probability) in behaviors) {
            cumulative += probability
            if (random <= cumulative) {
                return behavior
            }
        }

        return AnimalBehavior.GRAZING
    }

    fun cleanup() {
        stopSimulation()
        coroutineScope.cancel()
    }
}