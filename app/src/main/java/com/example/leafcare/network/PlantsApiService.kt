package com.example.leafcare.network

import com.example.leafcare.data.model.PlantCreateRequest
import com.example.leafcare.data.model.Plant
import com.example.leafcare.data.model.PlantNote
import com.example.leafcare.data.model.PlantNoteCreateRequest
import com.example.leafcare.data.model.PlantType
import com.example.leafcare.data.storage.TokenManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL =
    "http://10.0.2.2:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface PlantsApiService {

    @GET("/api/user/plants_get")
    suspend fun getMyPlants(): List<Plant>

    @GET("/api/user/plants_get_types")
    suspend fun getPlantTypes(): List<PlantType>

    @POST("/api/user/plants_create")
    suspend fun createPlant(@Body plantCreateRequest: PlantCreateRequest)

    @POST("/api/user/plants_delete")
    suspend fun deletePlant(@Query("plant_id") plantId: Int)

    @GET("/api/user/plants_get_growth_logs")
    suspend fun getPlantGrowthLogs(@Query("plant_id") plantId: Int): List<PlantNote>

    @POST("/api/user/plants_add_growth_log")
    suspend fun createPlantNote(@Body plantNoteCreateRequest: PlantNoteCreateRequest)

}

object PlantsApi {

    private lateinit var retrofit: Retrofit

    fun init(tokenManager: TokenManager) {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor(tokenManager)) // Attach access token
            .authenticator(TokenAuthenticator(tokenManager)) // Handle token refresh
            .build()

        retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(BASE_URL)
            .build()
    }

    val retrofitService: PlantsApiService by lazy {
        retrofit.create(PlantsApiService::class.java)
    }

}