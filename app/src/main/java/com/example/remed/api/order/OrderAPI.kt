package com.example.remed.api.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderAPI {
    @POST("patient/getOrders.php")
    suspend fun getOrders(
        @Header("Auth") authToken: String
    ) : Response<OrderList>

    @GET("patient/searchNearbyPharmacies.php")
    suspend fun searchNearbyPharmacies(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ) : Response<PharmacyList>
}