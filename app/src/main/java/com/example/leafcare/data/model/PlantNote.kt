package com.example.leafcare.data.model

import com.squareup.moshi.Json

class PlantNote(
    val id: Int,
//    @Json(name = "plant_id") val plantId: Int,
    @Json(name = "log_date") val logDate: String,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "notes") val noteText: String,
)