package com.example.remed.api.order

data class MedicineProduct(
    val ProductID: Int,
    val ProductName: String,
    val Quantity: Int,
    val UnitPrice: Double
)