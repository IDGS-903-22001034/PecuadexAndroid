package com.zurie.pecuadexproject.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceProductoEspacio
import com.zurie.pecuadexproject.Data.Model.ProductoEspacio
import com.zurie.pecuadexproject.State.ProductoEspacioState
import kotlinx.coroutines.launch

class ProductoEspacioViewModel : ViewModel() {
    var state by mutableStateOf(ProductoEspacioState())
        private set

    fun agregarProductoEspacio(
        productoEspacio: ProductoEspacio,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val response = ApiServiceProductoEspacio.getInstance().agregarProductoEspacio(productoEspacio)
                if (response.isSuccessful) {
                    onSuccess()
                    state = state.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    val errorMsg = "Error al agregar producto: ${response.code()}"
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

    fun obtenerProductosPorEspacio(espacioId: Long) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val productos = ApiServiceProductoEspacio.getInstance().obtenerProductosPorEspacio(espacioId)
                state = state.copy(
                    isLoading = false,
                    productosEspacio = productos,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error al obtener productos: ${e.message}"
                )
            }
        }
    }
}