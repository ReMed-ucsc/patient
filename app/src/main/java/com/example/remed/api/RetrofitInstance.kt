package com.example.remed.api

import com.example.remed.api.login.LoginApi
import com.example.remed.api.register.RegisterApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val baseUrl = "http://remed.atwebpages.com/API/"

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val loginApi : LoginApi = getInstance().create(LoginApi::class.java)
    val registerApi : RegisterApi = getInstance().create(RegisterApi::class.java)

}