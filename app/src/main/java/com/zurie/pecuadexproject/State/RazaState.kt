package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Raza

data class RazaState(
    val razas: List<Raza> = emptyList(),
    val isLoading: Boolean = false
)
