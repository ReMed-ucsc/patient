package com.example.remed.api.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login.php")
    suspend fun login(
        @Body loginRequest: Request
    ) : Response<LoginModel>
}