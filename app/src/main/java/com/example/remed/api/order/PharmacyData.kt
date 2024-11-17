package com.example.remed.api.order

data class PharmacyData(
    val PharmacyID: Int,
    val address: String,
    val contact: String,
    val distance: Double,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val availableCount: Int?
)

