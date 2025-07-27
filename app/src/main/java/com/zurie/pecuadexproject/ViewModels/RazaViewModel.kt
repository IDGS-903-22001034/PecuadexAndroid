package com.zurie.pecuadexproject.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceProductos
import com.zurie.pecuadexproject.Data.Api.ApiServiceRazas
import com.zurie.pecuadexproject.Data.Model.Producto
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.State.ProductoState
import com.zurie.pecuadexproject.State.RazaState
import kotlinx.coroutines.launch

class RazaViewModel : ViewModel() {
    var state by mutableStateOf(RazaState())
        private set

    var response: List<Raza> by mutableStateOf(listOf())
        private set

    init {
        obtenerProductos()
    }

    fun obtenerProductos() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val apiService = ApiServiceRazas.getInstance()
                val productosList = apiService.getRazas().items
                response = productosList

                state = state.copy(
                    isLoading = false,
                    razas = response
                )
            } catch (e: Exception) {

                state = state.copy(isLoading = false)
            }
        }
    }
}