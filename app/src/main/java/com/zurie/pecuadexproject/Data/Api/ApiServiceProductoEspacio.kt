package com.zurie.pecuadexproject.Data.Api

import com.zurie.pecuadexproject.Data.Model.ProductoEspacio
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServiceProductoEspacio {
    @POST("api/ProductoEspacios/AgregarProductoEspacio")
    suspend fun agregarProductoEspacio(@Body productoEspacio: ProductoEspacio): Response<ProductoEspacio>

    @GET("api/ProductoEspacios/ListaProductosPorEspacio/{espacioId}")
    suspend fun obtenerProductosPorEspacio(@Path("espacioId") espacioId: Long): List<ProductoEspacio>

    companion object {
        private var apiService: ApiServiceProductoEspacio? = null
        private const val BASE_URL = "http://192.168.1.108:7209/"

        fun getInstance(): ApiServiceProductoEspacio {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiServiceProductoEspacio::class.java)
            }
            return apiService!!
        }
    }
}