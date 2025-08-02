package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.ProductoResponse
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.Data.Model.RazaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiServiceRazas {
    @GET("api/Razas/ListaRazas")
    suspend fun getRazas(): List<Raza>

    companion object {
        private var apiService: ApiServiceRazas? = null
        private const val BASE_URL = "http://192.168.1.108:7209/"

        fun getInstance(): ApiServiceRazas {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceRazas::class.java)
            }
            return apiService!!
        }
    }
}