package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import com.zurie.pecuadexproject.Data.Model.Producto
import com.zurie.pecuadexproject.Data.Model.ProductoResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceProductos {
    @GET("api/Productos/ListaProductos")
    suspend fun getProductos(): List<Producto>

    companion object {
        private var apiService: ApiServiceProductos? = null
        private const val BASE_URL = "http://192.168.1.108:7209/"

        fun getInstance(): ApiServiceProductos {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceProductos::class.java)
            }
            return apiService!!
        }
    }
}