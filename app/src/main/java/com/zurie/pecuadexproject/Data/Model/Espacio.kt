package com.zurie.pecuadexproject.Data.Model

data class Espacio(
    var idEspacio: Long,
    var nombre: String = "",
    var direccion: String?,
    var estatus: Boolean,
    var razaId: Long,
)
