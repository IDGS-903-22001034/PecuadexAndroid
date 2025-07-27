package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Espacio

data class EspacioState(
    val espacios: List<Espacio> = emptyList(),
    val isLoading: Boolean = false,
    val espacioSeleccionado: Espacio? = null
)
