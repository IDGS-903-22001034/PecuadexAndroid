package com.zurie.pecuadexproject.Data.Model

import java.util.Date

data class ProductoEspacio(
    val idProductoEspacio: Long = 0, // Valor por defecto 0
    val productoId: Long,
    val espacioId: Long,
    val cantidadKilos: Double? = null,
    val fecha: String? = null
)
