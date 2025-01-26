package com.example.leafcare.data.model

import com.squareup.moshi.Json

data class Plant (
    val id: Int,
    val name: String,
    @Json(name = "plant_type") val plantType: String,
    val photo: String,
    val description: String,
//    val createdAt: String,
)