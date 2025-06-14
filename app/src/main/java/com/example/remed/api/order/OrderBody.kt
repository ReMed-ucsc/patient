package com.example.remed.api.order

data class OrderBody(
    val pharmacyID: Int,
    val productIDs: List<Int>,
    val quantities: List<Int>,
    val pickup: Int,
    val destination: String?,
    val destinationLat: Double?,
    val destinationLng: Double?,
    val comments: String?,
    val prescriptionUri: String?
)

data class SingleOrder(
    val OrderID: Int,
    val PharmacyID: Int,
    val pharmacyName: String,
    val status: String,
    val pickup: Int,
    val destination: String?,
    val date: String,
    val PatientID: Int,
    val patientName: String,
    val prescription: String?,
    val paymentMethod: String?
)

data class UpdateOrderBody(
    val orderID: Int,
    val productIDs: List<Int>,
    val quantities: List<Int>,
    val removedProductIDs: List<Int>,
    val status: String
)

data class OrderID(
    val orderID: Int
)