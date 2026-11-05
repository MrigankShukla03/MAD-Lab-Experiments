package com.example.tripjournal.models

data class Trip(
    val id: Long = 0,
    val title: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val notes: String,
    val coverPhotoUri: String?
)
