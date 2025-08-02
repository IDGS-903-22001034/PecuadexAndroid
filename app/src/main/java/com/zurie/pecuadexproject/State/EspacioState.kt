package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Espacio

data class EspacioState(
    val isLoading: Boolean = false,
    val espacios: List<Espacio> = emptyList(),
    val espacioSeleccionado: Espacio? = null,
    val error: String? = null
)

