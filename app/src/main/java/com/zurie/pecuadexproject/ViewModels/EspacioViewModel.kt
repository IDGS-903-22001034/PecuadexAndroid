package com.zurie.pecuadexproject.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceEspacios
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.State.EspacioState
import kotlinx.coroutines.launch

class EspacioViewModel : ViewModel() {
    var state by mutableStateOf(EspacioState())
        private set

    var response: List<Espacio> by mutableStateOf(listOf())
        private set

    init {
        obtenerEspacios()
    }

    fun obtenerEspacios() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val apiService = ApiServiceEspacios.getInstance()
                val espaciosList = apiService.getEspacios().items
                response = espaciosList

                state = state.copy(
                    isLoading = false,
                    espacios = response
                )
            } catch (e: Exception) {

                state = state.copy(isLoading = false)
            }
        }
    }
    fun obtenerEspacioId(id: Long) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val apiService = ApiServiceEspacios.getInstance()
                val espacio = apiService.getEspacio(id)

                state = state.copy(
                    isLoading = false,
                    espacioSeleccionado = espacio
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false)
            }
        }
    }
}