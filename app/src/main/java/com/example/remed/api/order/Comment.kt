package com.example.remed.api.order

data class Comment(
    val commentID: Int,
    val orderID: Int,
    val sender: String,
    val comments: String,
    val createdAt: String
)

data class CommentBody(
    val OrderID: Int,
    val sender: String,
    val comments: String,
)