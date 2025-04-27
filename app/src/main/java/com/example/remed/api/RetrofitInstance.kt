package com.example.remed.api

import com.example.remed.api.order.OrderAPI
import com.example.trainlog.api.auth.AuthAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val baseUrl = "https://becoming-adjusted-elf.ngrok-free.app/remed-1.0/api/"
//    http://remed.atwebpages.com/api/

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//     create api instances to call the methods inside of them
    val authAPI : AuthAPI = getInstance().create(AuthAPI::class.java)
    val orderAPI : OrderAPI = getInstance().create(OrderAPI::class.java)


}