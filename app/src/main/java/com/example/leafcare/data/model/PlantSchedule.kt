package com.example.leafcare.data.model

import com.squareup.moshi.Json

class PlantSchedule (
    @Json(name = "plant_type") val plantType: String,
    val date: String
)