package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.AnimalResponse
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.Data.Model.EspacioResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceEspacios {

    @GET("api/ListaEspacios")
    suspend fun getEspacios(): EspacioResponse

    @GET("api/getEspacio/{id}")
    suspend fun getEspacio(@Path("id") id: Long): Espacio

    @POST("api/AgregarEspacios")
    fun agregarEspacio(@Body espacio: Espacio): Call<Espacio>

    @PUT("api/ModificarEspacio/{id}")
    fun actualizarEspacio(@Path("id") id: Int, @Body espacio: Espacio): Call<Espacio>

    @DELETE("api/EliminarEspacio/{id}")
    fun eliminarEspacio(@Path("id") id: Int): Call<Void>

    companion object {
        private var apiService: ApiServiceEspacios? = null
        private const val BASE_URL = "http://localhost:7209/"

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