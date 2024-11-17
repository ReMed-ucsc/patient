package com.example.remed.api.order

data class OrderData(
    val DeliveryID: Int,
    val OrderID: Int,
    val PatientID: Int,
    val PharmacyID: Int,
    val date: String,
    val destination: String,
    val pickup: String,
    val status: String
)

data class Order(
    val orderDetails: OrderData,
    val productDetails: List<MedicineProduct>
)