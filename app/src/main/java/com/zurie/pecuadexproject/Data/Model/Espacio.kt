package com.zurie.pecuadexproject.Data.Model

data class Espacio(
    var idEspacio: Long = 0,
    var nombre: String = "",
    var direccion: String = "",
    var estatus: Boolean = true,  // ← CAMBIA de String a Boolean
    var raza: Raza? = null,
    var razaId: Long = 0,
    var animales: List<Animal>? = null
)
