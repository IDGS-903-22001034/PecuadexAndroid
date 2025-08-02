package com.zurie.pecuadexproject.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceEnfermedades
import com.zurie.pecuadexproject.Data.Model.Enfermedad
import com.zurie.pecuadexproject.State.EnfermedadState
import kotlinx.coroutines.launch

class EnfermedadViewModel : ViewModel() {
    var state by mutableStateOf(EnfermedadState())
        private set

    init {
        obtenerEnfermedades()
    }

    fun obtenerEnfermedades() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val apiService = ApiServiceEnfermedades.getInstance()
                val enfermedades = apiService.getEnfermedades()
                state = state.copy(
                    isLoading = false,
                    enfermedades = enfermedades
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false)
                Log.e("EnfermedadViewModel", "Error al obtener enfermedades: ${e.message}")
            }
        }
    }
}