package com.example.leafcare.data.model

import com.squareup.moshi.Json

data class AuthResponse (
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String
)
