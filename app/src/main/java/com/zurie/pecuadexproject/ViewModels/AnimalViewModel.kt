package com.zurie.pecuadexproject.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurie.pecuadexproject.Data.Api.ApiServiceAnimales
import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.State.AnimalState
import kotlinx.coroutines.launch

class AnimalViewModel : ViewModel() {

    var state by mutableStateOf(AnimalState())
        private set

    var response: List<Animal> by mutableStateOf(listOf())
        private set

    init {
        obtenerAnimales()
    }

    fun obtenerAnimales() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val apiService = ApiServiceAnimales.getInstance()
                val animalesList = apiService.getAnimales().items
                response = animalesList

                state = state.copy(
                    isLoading = false,
                    animales = response
                )
            } catch (e: Exception) {

                state = state.copy(isLoading = false)
            }
        }
    }
}