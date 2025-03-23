package com.example.trainlog.api.auth

import com.example.remed.api.auth.AuthResult
import com.example.remed.api.auth.LoginModel
import com.example.remed.api.auth.LoginRequest
import com.example.remed.api.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {
    @POST("patient/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<LoginModel>

    @POST("patient/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ) : Response<LoginModel>
}