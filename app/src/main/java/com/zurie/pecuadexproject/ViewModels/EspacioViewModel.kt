package com.zurie.pecuadexproject.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceEspacios
import com.zurie.pecuadexproject.Data.Api.ApiServiceRazas
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.State.EspacioState
import kotlinx.coroutines.launch

class EspacioViewModel : ViewModel() {
    var state by mutableStateOf(EspacioState())
        private set

    init {
        obtenerEspacios()
    }

    fun obtenerEspacios() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val espacios = ApiServiceEspacios.getInstance().getEspacios()
                val razas = ApiServiceRazas.getInstance().getRazas()

                val espaciosCompletos = espacios.map { espacio ->
                    val razaCompleta = razas.firstOrNull { it.idRaza == espacio.razaId }
                    espacio.copy(raza = razaCompleta)
                }

                state = state.copy(
                    isLoading = false,
                    espacios = espaciosCompletos,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    fun obtenerEspacioId(id: Long) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val espacio = ApiServiceEspacios.getInstance().getEspacio(id)
                state = state.copy(
                    isLoading = false,
                    espacioSeleccionado = espacio
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error al obtener espacio: ${e.message}"
                )
            }
        }
    }

    fun guardarEspacio(espacio: Espacio, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val response = ApiServiceEspacios.getInstance().agregarEspacio(espacio)
                if (response.isSuccessful) {
                    obtenerEspacios()
                    onSuccess()
                    state = state.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    val errorMsg = "Error al guardar espacio: ${response.code()}"
                    onError(errorMsg)
                    state = state.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.message}"
                onError(errorMsg)
                state = state.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }

    fun modificarEspacio(id: Long, espacio: Espacio, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val response = ApiServiceEspacios.getInstance().actualizarEspacio(id, espacio)
                if (response.isSuccessful) {
                    // Verifica si hay cuerpo en la respuesta
                    response.body()?.let {
                        obtenerEspacios()
                        obtenerEspacioId(id)
                        onSuccess()
                    } ?: run {
                        onError("Respuesta vacía del servidor")
                    }
                    state = state.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    val errorMsg = try {
                        "Error al modificar espacio: ${response.code()} - ${response.errorBody()?.string()}"
                    } catch (e: Exception) {
                        "Error al modificar espacio: ${response.code()}"
                    }
                    onError(errorMsg)
                    state = state.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.message}"
                onError(errorMsg)
                state = state.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }

    fun eliminarEspacio(id: Long, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val response = ApiServiceEspacios.getInstance().eliminarEspacio(id)

                if (response.isSuccessful) {
                    obtenerEspacios()
                    onSuccess()
                    state = state.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    val errorMsg = "Error al eliminar espacio: ${response.code()}"
                    onError(errorMsg)
                    state = state.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.message}"
                onError(errorMsg)
                state = state.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }

    fun cambiarEstatusEspacio(id: Long, nuevoEstatus: Boolean) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val espacioActual = state.espacioSeleccionado?.copy(estatus = nuevoEstatus)
                if (espacioActual != null) {
                    val response = ApiServiceEspacios.getInstance().actualizarEspacio(id, espacioActual)
                    if (response.isSuccessful) {
                        obtenerEspacios()
                        obtenerEspacioId(id)
                        state = state.copy(
                            isLoading = false,
                            error = null
                        )
                    } else {
                        state = state.copy(
                            isLoading = false,
                            error = "Error al cambiar estatus"
                        )
                    }
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error de red: ${e.message}"
                )
            }
        }
    }
}