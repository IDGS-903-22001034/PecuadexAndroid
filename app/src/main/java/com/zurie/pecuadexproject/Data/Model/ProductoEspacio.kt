package com.zurie.pecuadexproject.Data.Model

import java.util.Date

data class ProductoEspacio(
    var idProductoEspacio: Long,
    var productoId: Long,
    var espacioId: Long,
    var cantidadKilos: Double,
    var fecha: Date
)
