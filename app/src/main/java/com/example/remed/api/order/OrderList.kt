package com.example.remed.api.order

data class OrderList(
    val `data`: List<OrderData>,
    val result: Result
)

data class CreateOrder(
    val `data`: OrderID,
    val result: Result
)

data class OrderResult(
    val `data`: Order,
    val result: Result
)

data class CommentResult(
    val `data`: OrderID,
    val result: Result
)
