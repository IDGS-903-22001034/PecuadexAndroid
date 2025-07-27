package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import retrofit2.Call
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceAnimales {
    @GET("api/ListaAnimales")
    suspend fun getAnimales(): AnimalResponse

    @POST("api/AgregarAnimal")
    fun agregarAnimal(@Body animal: Animal): Call<Animal>

    @PUT("api/ModificarAnimal/{id}")
    fun actualizarAnimal(@Path("id") id: Int, @Body animal: Animal): Call<Animal>

    @DELETE("api/EliminarAnimal/{id}")
    fun eliminarAnimal(@Path("id") id: Int): Call<Void>

    companion object {
        private var apiService: ApiServiceAnimales? = null
        private const val BASE_URL = "http://localhost:7209/"

        fun getInstance(): ApiServiceAnimales {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceAnimales::class.java)
            }
            return apiService!!
        }
    }
}