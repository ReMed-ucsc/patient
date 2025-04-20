package com.example.remed.api.order

data class OrderData(
    val DeliveryID: Int,
    val OrderID: Int,
    val PatientID: Int,
    val PharmacyID: Int,
    val date: String,
    val destination: String,
    val pickup: String,
    val status: String,
    val paymentMethod: String?,
)

data class Order(
    val orderDetails: SingleOrder,
    val productDetails: List<MedicineProduct>,
    val comments: List<Comment>
)