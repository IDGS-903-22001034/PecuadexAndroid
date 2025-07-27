package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import com.zurie.pecuadexproject.Data.Model.EnfermedadResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceEnfermedades {

    @GET("api/ListaEnfermedades")
    suspend fun getEnfermedades(): EnfermedadResponse

    companion object {
        private var apiService: ApiServiceEnfermedades? = null
        private const val BASE_URL = "http://localhost:7209/"

        fun getInstance(): ApiServiceEnfermedades {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceEnfermedades::class.java)
            }
            return apiService!!
        }
    }
}