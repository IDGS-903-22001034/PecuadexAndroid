package com.zurie.pecuadexproject.Data.Model

data class Animal(
    val idAnimal: Long = 0,
    val apodo: String = "",
    val critico: Boolean = false,
    val fechaFallecimiento: String? = null,
    val fechaNacimiento: String = "",
    val peso: Int = 0,
    val sexo: String = "",
    val razaId: Int? = null,
    val espacioId: Int? = null
)
