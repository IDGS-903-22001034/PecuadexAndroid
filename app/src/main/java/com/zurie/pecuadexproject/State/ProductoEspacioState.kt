package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.Data.Model.ProductoEspacio

data class ProductoEspacioState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val productosEspacio: List<ProductoEspacio> = emptyList(),
    val espacioSeleccionado: Espacio? = null // Cambiado a nullable
)