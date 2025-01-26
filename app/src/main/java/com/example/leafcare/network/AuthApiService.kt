package com.example.leafcare.network

import com.example.leafcare.data.model.AuthResponse
import com.example.leafcare.data.model.LoginRequest
import com.example.leafcare.data.model.RefreshTokenRequest
import com.example.leafcare.data.model.RegisterRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL =
    "http://10.0.2.2:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface AuthApiService {

    @POST("/api/auth/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): AuthResponse

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("/api/auth/refresh")
    fun refreshTokens(@Body refreshTokenRequest: RefreshTokenRequest): Call<AuthResponse>

}

object AuthApi {
    val retrofitService : AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java) }
}