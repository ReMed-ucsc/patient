package com.example.remed.api.order

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface OrderAPI {
    @POST("patient/getOrders")
    suspend fun getOrders(
        @Header("Auth") authToken: String
    ) : Response<OrderList>

    @GET("patient/searchNearbyPharmacies")
    suspend fun searchNearbyPharmacies(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("range") range: Int
    ) : Response<PharmacyList>

    @GET("patient/searchPharmacies")
    suspend fun searchPharmacies(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("productIDs") productIDs: String,
        @Query("range") range: Int
    ) : Response<PharmacyList>

    @GET("medicine/getMedicines")
    suspend fun getMedicines(
        @Query("search") search: String
    ) : Response<MedicineList>

    @GET("medicine/getPharmacyMedicines")
    suspend fun getPharmacyMedicines(
        @Query("pharmacyID") pharmacyID: Int,
        @Query("search") search: String
    ) : Response<MedicineList>

    @GET("medicine/checkForOverTheCounter")
    suspend fun checkForOverTheCounter(
        @Query("productIDs") productIDs: String
    ) : Response<CheckOverTheCounter>

    @Multipart
    @POST("order/createOrder")
    suspend fun placeOrder(
        @Header ("Auth") authToken: String,
        @Part ("order") order: RequestBody,
        @Part prescription: MultipartBody.Part?
    ) : Response<CreateOrder>

    @POST("order/updateOrder")
    suspend fun updateOrder(
        @Header ("Auth") authToken: String,
        @Body order: UpdateOrderBody
    ) : Response<CreateOrder>

    @GET("order/updateOrderStatus")
    suspend fun updateOrderStatus(
        @Header ("Auth") authToken: String,
        @Query("OrderID") orderID: Int,
        @Query("status") status: String
    ) : Response<CreateOrder>

    @GET("order/setPaymentMethod")
    suspend fun setPaymentMethod(
        @Header ("Auth") authToken: String,
        @Query("OrderID") orderID: Int,
        @Query("paymentMethod") paymentMethod: String
    ) : Response<CreateOrder>

    @GET("order/getOrder")
    suspend fun getOrder(
        @Header("Auth") authToken: String,
        @Query("OrderID") orderID: Int
    ) : Response<OrderResult>

    @POST("order/commentOrder")
    suspend fun sendMessage(
        @Header("Auth") authToken: String,
        @Body messageBody: CommentBody
    ): Response<CommentResult>
}