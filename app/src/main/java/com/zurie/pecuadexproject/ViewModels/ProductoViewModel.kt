package com.zurie.pecuadexproject.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceProductos
import com.zurie.pecuadexproject.Data.Model.Producto
import com.zurie.pecuadexproject.State.ProductoState
import kotlinx.coroutines.launch

class ProductoViewModel: ViewModel() {
    var state by mutableStateOf(ProductoState())
        private set

    var response: List<Producto> by mutableStateOf(listOf())
        private set

    init {
        obtenerProductos()
    }

    fun obtenerProductos() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val apiService = ApiServiceProductos.getInstance()
                val productosList = apiService.getProductos()
                response = productosList
                state = state.copy(
                    isLoading = false,
                    productos = productosList
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false)
                Log.e("ProductoViewModel", "Error al obtener productos: ${e.message}")
            }
        }
    }

}