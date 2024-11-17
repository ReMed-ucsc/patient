package com.example.remed.api.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderAPI {
    @POST("patient/getOrders")
    suspend fun getOrders(
        @Header("Auth") authToken: String
    ) : Response<OrderList>

    @GET("patient/searchNearbyPharmacies")
    suspend fun searchNearbyPharmacies(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ) : Response<PharmacyList>

    @GET("patient/searchPharmacies")
    suspend fun searchPharmacies(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("productIDs") productIDs: List<Int>
    ) : Response<PharmacyList>

    @GET("medicine/getMedicines")
    suspend fun getMedicines(
        @Query("search") search: String
    ) : Response<MedicineList>

    @POST("order/createOrder")
    suspend fun placeOrder(
        @Header("Auth") authToken: String,
        @Body order: OrderBody
    ) : Response<CreateOrder>

    @GET("order/getOrder")
    suspend fun getOrder(
        @Header("Auth") authToken: String,
        @Query("OrderID") orderID: Int
    ) : Response<OrderResult>
}