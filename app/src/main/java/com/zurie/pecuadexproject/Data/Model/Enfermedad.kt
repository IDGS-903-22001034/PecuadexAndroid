package com.zurie.pecuadexproject.Data.Model

data class Enfermedad(
    var idEnfermedad: Long,
    var nombre: String = "",
    var descripcion: String?,
    var frecuencia: Int?,
    var prevencion: String?,
    var tratamiento: String?
)
