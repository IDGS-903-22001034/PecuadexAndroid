package com.zurie.pecuadexproject.State

import com.zurie.pecuadexproject.Data.Model.Animal

data class AnimalState(
    val animales: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val onSuccess: () -> Unit = {}
)
