package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Producto

data class ProductoState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = false
)
