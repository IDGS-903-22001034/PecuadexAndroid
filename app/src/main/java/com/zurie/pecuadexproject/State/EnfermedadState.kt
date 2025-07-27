package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Enfermedad

data class EnfermedadState(
    val enfermedades: List<Enfermedad> = emptyList(),
    val isLoading: Boolean = false
)
