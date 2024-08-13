package com.example.remed.api.register

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("register.php")
    suspend fun register(
        @Body registerRequest: Request
    ) : Response<Result>

}