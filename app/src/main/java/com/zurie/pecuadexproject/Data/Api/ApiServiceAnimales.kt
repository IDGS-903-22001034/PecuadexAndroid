package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import com.zurie.pecuadexproject.Data.Model.Espacio
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceAnimales {
    @GET("api/Animales/ListaAnimales")
    suspend fun getAnimales(): List<Animal>

    @POST("api/Animales/AgregarAnimal")
    suspend fun agregarAnimal(@Body animal: Animal): Response<Animal>

    @GET("api/Animales/ObtenerAnimal/{id}")
    suspend fun obtenerAnimalPorId(@Path("id") id: Long): Animal

    @GET("api/Animales/ObtenerEnfermedadesAnimal/{id}")
    suspend fun obtenerEnfermedadesDelAnimal(@Path("id") id: Long): List<Long>

    @PUT("api/Animales/ActualizarEnfermedadesAnimal/{id}")
    suspend fun actualizarEnfermedadesAnimal(
        @Path("id") id: Long,
        @Body enfermedadesIds: List<Long>
    ): Response<Unit>

    @PUT("api/Animales/ModificarAnimal/{id}")
    suspend fun modificarAnimal(
        @Path("id") id: Long,
        @Body animal: Animal
    ): Response<Unit>

    @DELETE("api/Animales/EliminarAnimal/{id}")
    suspend fun eliminarAnimal(@Path("id") id: Long): Response<Unit>

    companion object {
        private var apiService: ApiServiceAnimales? = null
        private const val BASE_URL = "http://172.20.10.2:7209/"

        fun getInstance(): ApiServiceAnimales {
            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                apiService = retrofit.create(ApiServiceAnimales::class.java)
            }
            return apiService!!
        }
    }
}