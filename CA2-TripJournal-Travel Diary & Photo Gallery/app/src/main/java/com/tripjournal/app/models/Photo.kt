package com.tripjournal.app.models

data class Photo(
    val id: Long = 0,
    val tripId: Long,
    val photoUri: String
)
