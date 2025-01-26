package com.example.leafcare.data.model

import com.squareup.moshi.Json

data class RefreshTokenRequest(
    @Json(name = "refresh_token") val refreshToken: String
)