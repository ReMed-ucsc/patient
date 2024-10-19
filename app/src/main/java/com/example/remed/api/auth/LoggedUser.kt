package com.example.remed.api.auth

data class LoggedUser(
    val auth_token: String,
    val email: String,
    val name: String
)