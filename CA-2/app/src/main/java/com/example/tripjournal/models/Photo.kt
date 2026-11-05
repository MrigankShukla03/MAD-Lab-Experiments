package com.example.tripjournal.models

data class Photo(
    val id: Long = 0,
    val tripId: Long,
    val photoUri: String
)
