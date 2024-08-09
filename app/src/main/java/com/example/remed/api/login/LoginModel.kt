package com.example.remed.api.login

data class LoginModel(
    val request: Request,
    val result: Result,
    val user: User,
)