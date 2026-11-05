package com.tripjournal.app.models

data class Trip(
    val id: Long = 0,
    val title: String,
    val place: String,
    val notes: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val coverPhotoUri: String?
)
