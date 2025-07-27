package com.zurie.pecuadexproject.ViewModels

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

    var response: List<Enfermedad> by mutableStateOf(listOf())
        private set

    init {
        obtenerEnfermedades()
    }

    fun obtenerEnfermedades() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val apiService = ApiServiceEnfermedades.getInstance()
                val enfermedadesList = apiService.getEnfermedades().items
                response = enfermedadesList

                state = state.copy(
                    isLoading = false,
                    enfermedades = response
                )
            } catch (e: Exception) {

                state = state.copy(isLoading = false)
            }
        }
    }
}