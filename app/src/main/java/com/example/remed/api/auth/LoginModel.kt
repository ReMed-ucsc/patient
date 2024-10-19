package com.example.remed.api.auth

data class LoginModel(
    val request: LoginRequest,
    val result: AuthResult,
    val user: LoggedUser,
)