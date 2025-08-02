package com.zurie.pecuadexproject.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceAnimales
import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.State.AnimalState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnimalViewModel : ViewModel() {
    var state by mutableStateOf(AnimalState())
        private set

    var animalParaEditar by mutableStateOf<Animal?>(null)
        private set

    var guardadoExitoso by mutableStateOf(false)
        private set


    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarAnimal(
        animal: Animal,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            guardadoExitoso = false

            try {
                // Convertir fecha de formato dd/MM/yyyy a yyyy-MM-dd
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val fechaNacimiento = if (animal.fechaNacimiento.isNotEmpty()) {
                    val localDate = LocalDate.parse(animal.fechaNacimiento, inputFormatter)
                    localDate.format(outputFormatter)
                } else {
                    ""
                }

                // Crear copia del animal con la fecha formateada
                val animalParaEnviar = animal.copy(
                    fechaNacimiento = fechaNacimiento
                )

                val apiService = ApiServiceAnimales.getInstance()
                val response = apiService.agregarAnimal(animalParaEnviar)

                if (response.isSuccessful) {
                    guardadoExitoso = true
                    obtenerAnimales()
                    onSuccess()
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos"
                        404 -> "Recurso no encontrado"
                        500 -> "Error del servidor"
                        else -> "Error al agregar animal: ${response.code()}"
                    }
                    state = state.copy(error = errorMsg)
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de formato de fecha o conexión: ${e.message ?: "Error desconocido"}"
                state = state.copy(error = errorMsg)
                onError(errorMsg)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun obtenerAnimales() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val apiService = ApiServiceAnimales.getInstance()
                val animales = apiService.getAnimales()
                state = state.copy(
                    isLoading = false,
                    animales = animales,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error al cargar animales: ${e.message}"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarAnimal(id: Long, animal: Animal, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // Convertir fecha de formato dd/MM/yyyy a yyyy-MM-dd
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val fechaNacimiento = if (animal.fechaNacimiento.isNotEmpty()) {
                    try {
                        val localDate = LocalDate.parse(animal.fechaNacimiento, inputFormatter)
                        localDate.format(outputFormatter)
                    } catch (e: Exception) {
                        // Si ya está en formato yyyy-MM-dd, dejarlo como está
                        animal.fechaNacimiento
                    }
                } else {
                    ""
                }

                // Crear copia del animal con la fecha formateada
                val animalParaEnviar = animal.copy(
                    fechaNacimiento = fechaNacimiento
                )

                val apiService = ApiServiceAnimales.getInstance()
                val response = apiService.modificarAnimal(id, animalParaEnviar)

                if (response.isSuccessful) {
                    obtenerAnimales()
                    onSuccess()
                } else {
                    onError("Error al modificar animal: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error de formato de fecha o red: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun eliminarAnimal(id: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val apiService = ApiServiceAnimales.getInstance()
                val response = apiService.eliminarAnimal(id)

                if (response.isSuccessful) {
                    obtenerAnimales()
                    onSuccess()
                } else {
                    onError("Error al eliminar animal: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun obtenerAnimalPorId(id: Long) {
        try {
            val apiService = ApiServiceAnimales.getInstance()
            val animal = apiService.obtenerAnimalPorId(id)

            // Convertir fecha de formato yyyy-MM-dd a dd/MM/yyyy
            animalParaEditar = if (animal.fechaNacimiento.isNotEmpty()) {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                try {
                    val localDate = LocalDate.parse(animal.fechaNacimiento, inputFormatter)
                    animal.copy(fechaNacimiento = localDate.format(outputFormatter))
                } catch (e: Exception) {
                    // Si ya está en formato dd/MM/yyyy, dejarlo como está
                    animal
                }
            } else {
                animal
            }
        } catch (e: Exception) {
            state = state.copy(error = "Error al cargar animal: ${e.message}")
        }
    }

    // En AnimalViewModel.kt
    suspend fun obtenerEnfermedadesDelAnimal(animalId: Long): List<Long> {
        return try {
            // Implementa esta función en tu ApiServiceAnimales
            val apiService = ApiServiceAnimales.getInstance()
            apiService.obtenerEnfermedadesDelAnimal(animalId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // En AnimalViewModel.kt
    fun actualizarEnfermedadesAnimal(
        animalId: Long,
        enfermedadesIds: List<Long>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val apiService = ApiServiceAnimales.getInstance()
                val response = apiService.actualizarEnfermedadesAnimal(animalId, enfermedadesIds)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al actualizar enfermedades: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun contarCrias(): Int {
        return state.animales.count { it.esCria() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun contarAdultos(): Int {
        return state.animales.count { !it.esCria() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun animalescriticos(): Boolean {
        return state.animales.any { it.critico }
    }
}