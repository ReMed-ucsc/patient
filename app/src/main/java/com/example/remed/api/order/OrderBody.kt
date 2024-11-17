package com.example.remed.api.order

data class OrderBody(
    val pharmacyID: Int,
    val productIDs: List<Int>,
    val quantities: List<Int>,
    val pickup: Boolean,
    val destination: String?,
    val comments: String?,
    val prescriptionUri: String?
)

data class OrderID(
    val orderID: Int
)