package com.zurie.pecuadexproject.ViewModels

import android.util.Log
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
            obtenerRazas()
        }
        fun obtenerRazas() {
            viewModelScope.launch {
                state = state.copy(isLoading = true)
                try {
                    val apiService = ApiServiceRazas.getInstance()
                    val razasList = apiService.getRazas()
                    response = razasList
                    state = state.copy(
                        isLoading = false,
                        razas = razasList
                    )
                } catch (e: Exception) {
                    state = state.copy(isLoading = false)
                    Log.e("RazaViewModel", "Error al obtener razas: ${e.message}")
                }
            }
        }
}