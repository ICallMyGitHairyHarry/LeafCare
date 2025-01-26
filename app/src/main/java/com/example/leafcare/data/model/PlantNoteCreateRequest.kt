package com.example.leafcare.data.model

import com.squareup.moshi.Json

class PlantNoteCreateRequest (
    @Json(name = "plant_id") val plantId: Int,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "notes") val noteText: String,
)