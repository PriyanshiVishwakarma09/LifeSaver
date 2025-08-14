package com.example.lifesaver.data

class EmergencyRequest(
    val userId: String = "",
    val type: String = "",
    val timestamp: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)