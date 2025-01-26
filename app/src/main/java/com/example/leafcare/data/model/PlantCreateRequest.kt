package com.example.leafcare.data.model

import com.squareup.moshi.Json


data class PlantCreateRequest (
    val name: String,
    @Json(name = "plant_type") val plantType: String,
    val photo: String,
    val description: String,
    @Json(name = "height_cm") val heightCm: Double,
//    val createdAt: String,
)