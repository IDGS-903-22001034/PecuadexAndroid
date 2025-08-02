package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.Data.Model.EspacioResponse
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

interface ApiServiceEspacios {

    @GET("api/Espacios/ListaEspacios")
    suspend fun getEspacios(): List<Espacio>

    @GET("api/Espacios/getEspacio/{id}")
    suspend fun getEspacio(@Path("id") id: Long): Espacio

    @POST("api/Espacios/AgregarEspacios")
    suspend fun agregarEspacio(@Body espacio: Espacio): Response<Espacio>

    @PUT("api/Espacios/ModificarEspacio/{id}")
    suspend fun actualizarEspacio(@Path("id") id: Long, @Body espacio: Espacio): Response<Espacio>

    @DELETE("api/Espacios/EliminarEspacio/{id}")
    suspend fun eliminarEspacio(@Path("id") id: Long): Response<Unit>

    companion object {
        private var apiService: ApiServiceEspacios? = null
        private const val BASE_URL = "http://192.168.1.108:7209/"

        fun getInstance(): ApiServiceEspacios {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceEspacios::class.java)
            }
            return apiService!!
        }
    }
}