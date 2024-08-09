package com.example.remed.api.login

data class User(
    val auth_token: String,
    val email: String,
    val name: String
)