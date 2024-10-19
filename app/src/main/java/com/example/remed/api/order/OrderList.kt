package com.example.remed.api.order

data class OrderList(
    val `data`: List<OrderData>,
    val result: Result
)