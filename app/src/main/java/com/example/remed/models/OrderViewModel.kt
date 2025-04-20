package com.example.remed.models

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.order.CheckOverTheCounter
import com.example.remed.api.order.Comment
import com.example.remed.api.order.CommentBody
import com.example.remed.api.order.CommentResult
import com.example.remed.api.order.CreateOrder
import com.example.remed.api.order.MedicineList
import com.example.remed.api.order.OrderBody
import com.example.remed.api.order.OrderList
import com.example.remed.api.order.OrderResult
import com.example.remed.api.order.PharmacyList
import com.example.remed.api.order.Result
import com.example.remed.api.order.UpdateOrderBody
import com.example.remed.util.uploadImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.remed.util.uriToFile

class OrderViewModel : ViewModel() {
    private val orderAPI = RetrofitInstance.orderAPI

    private val _ordersResponse = MutableLiveData<NetworkResponse<OrderList>>()
    val ordersResponse: LiveData<NetworkResponse<OrderList>> = _ordersResponse

//    nearby pharmacies
    private val _pharmacyListResponse = MutableLiveData<NetworkResponse<PharmacyList>>()
    val pharmacyListResponse: LiveData<NetworkResponse<PharmacyList>> = _pharmacyListResponse

    private val _pharmacyWithMedicineListResponse = MutableLiveData<NetworkResponse<PharmacyList>>()
    val pharmacyWithMedicineListResponse: LiveData<NetworkResponse<PharmacyList>> = _pharmacyWithMedicineListResponse

    private val _medicineListResponse = MutableLiveData<NetworkResponse<MedicineList>>()
    val medicineListResponse: LiveData<NetworkResponse<MedicineList>> = _medicineListResponse

    private val _checkForOverTheCounterResponse = MutableLiveData<NetworkResponse<CheckOverTheCounter>>()
    val checkForOverTheCounterResponse: LiveData<NetworkResponse<CheckOverTheCounter>> = _checkForOverTheCounterResponse

    private val _createOrderResponse = MutableLiveData<NetworkResponse<CreateOrder>>()
    val createOrderResponse: LiveData<NetworkResponse<CreateOrder>> = _createOrderResponse

    private val _updateOrderResponse = MutableLiveData<NetworkResponse<CreateOrder>>()
    val updateOrderResponse: LiveData<NetworkResponse<CreateOrder>> = _updateOrderResponse

    private val _getOrderResponse = MutableLiveData<NetworkResponse<OrderResult>>()
    val getOrderResponse: LiveData<NetworkResponse<OrderResult>> = _getOrderResponse

    private val _getMessageResponse = MutableLiveData<NetworkResponse<CommentResult>>()
    val getMessageResponse: LiveData<NetworkResponse<CommentResult>> = _getMessageResponse

    fun getOrders(accessToken: String) {
        _ordersResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.getOrders("Bearer $accessToken")
                Log.d("OrderViewModel", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
//                        no errors while login
                        if (!it.result.error) {
                            _ordersResponse.value = NetworkResponse.Success(it)
                        } else {
                            _ordersResponse.value = NetworkResponse.Error("Failed: ${it.result.message }")
                        }
                    }
                } else {
                    _ordersResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _ordersResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun searchNearbyPharmacies(lat: Double, long: Double, range: Int = 10) {
        _pharmacyListResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.searchNearbyPharmacies(lat, long, range)
                Log.d("SearchPharmacy", "Response Body for nearby: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _pharmacyListResponse.value = NetworkResponse.Success(it)
                        } else {
                            _pharmacyListResponse.value = NetworkResponse.Error("Failed: ${it.result.message }")
                        }
                    }
                } else {
                    _pharmacyListResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _pharmacyListResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun searchPharmacies(lat: Double = 6.84862699, long: Double = 79.924950, range: Int, productIDs: List<Int>) {
        _pharmacyWithMedicineListResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try { // Log the productIDs

                val response = orderAPI.searchPharmacies(lat, long, productIDs.joinToString(","), range)
                Log.d("SearchPharmacy", "Response Body for search med: ${response.body()} productIDs: $productIDs")
//                Log.d("SearchPharmacy", "Raw Response Body for search med: ${response.raw()}")
                if (response.isSuccessful) {
                    val rawResponse = response.raw().toString()
                    Log.d("API Response", rawResponse) // Log the raw response
                    response.body()?.let {
                        if (!it.result.error) {
                            _pharmacyWithMedicineListResponse.value = NetworkResponse.Success(it)
                        } else {
                            _pharmacyWithMedicineListResponse.value = NetworkResponse.Error("Failed: ${it.result.message }")
                        }
                    }
                } else {
                    _pharmacyWithMedicineListResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _pharmacyWithMedicineListResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun getMedicines(search: String = "") {
        _medicineListResponse.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = orderAPI.getMedicines(search)
                Log.d("MedicineList", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _medicineListResponse.value = NetworkResponse.Success(it)
                            Log.d("MedicineList", "Medicines: ${it.data}")
                        } else {
                            _medicineListResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            Log.d("MedicineList", "Failed: ${it.result.message}")
                        }
                    }
                } else {
                    _medicineListResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    Log.d("MedicineList", "Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _medicineListResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                Log.d("MedicineList", "Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun getPharmacyMedicines(pharmacyID: Int, search: String = "") {
        _medicineListResponse.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                Log.d("PharmacyMedicines", "Pharmacy ID: $pharmacyID, Search: $search") // Log the pharmacy ID and search query
                val response = orderAPI.getPharmacyMedicines(pharmacyID, search)
                Log.d("MedicineList", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _medicineListResponse.value = NetworkResponse.Success(it)
                            Log.d("MedicineList", "Medicines: ${it.data}")
                        } else {
                            _medicineListResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            Log.d("MedicineList", "Failed: ${it.result.message}")
                        }
                    }
                } else {
                    _medicineListResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    Log.d("MedicineList", "Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _medicineListResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                Log.d("MedicineList", "Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun checkForOverTheCounter(productIDs: List<Int>){
        _checkForOverTheCounterResponse.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val productIDsString = productIDs.joinToString(",")
                Log.d("CheckOverTheCounter", "Product IDs: $productIDsString") // Log the product IDs

                val response = orderAPI.checkForOverTheCounter(productIDsString)
                Log.d("CheckOverTheCounter", "Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data == 0) {
                            _checkForOverTheCounterResponse.value = NetworkResponse.Success(it)
                            Log.d("CheckOverTheCounter", "Over the counter: ${it.result.message}")
                        } else {
                            _checkForOverTheCounterResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            Log.d("CheckOverTheCounter", "Failed: ${it.result.message}")
                        }
                    }
                } else {
                    _checkForOverTheCounterResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    Log.d("CheckOverTheCounter", "Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _checkForOverTheCounterResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                Log.d("CheckOverTheCounter", "Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun createOrder(context: Context, accessToken: String, orderBody: OrderBody, prescriptionUri: Uri?, onResult: (Boolean) -> Unit) {
        _createOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
//            val accessTokenBody = accessToken.toRequestBody("text/plain".toMediaTypeOrNull())
            val orderBodyJson = Gson().toJson(orderBody)
            Log.d("CreateOrder", "OrderBody JSON: $orderBodyJson") // Log the JSON payload
            val orderBodyRequest = orderBodyJson.toRequestBody("application/json".toMediaTypeOrNull())

            val prescriptionPart = prescriptionUri?.let {
                val file = uploadImage(context, it)
                val requestFile: RequestBody = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("prescription", file.name, requestFile)
            }

            try {
                val response = orderAPI.placeOrder("Bearer $accessToken", orderBodyRequest, prescriptionPart)
                val rawResponseBody = response.errorBody()?.string() ?: response.body().toString()
                Log.d("CreateOrder", "Raw Response Body: $rawResponseBody")
                onResult(response.isSuccessful)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _createOrderResponse.value = NetworkResponse.Success(response.body()!!)
//                            onResult(true);
                            Log.d("CreateOrder", "Order: ${it.data}")
                        } else {
//                            onResult(false);
                            _createOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            Log.d("CreateOrder", "Failed: ${it.result.message}")
                        }
                    }
                } else {
//                    onResult(false);
                    _createOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    Log.d("CreateOrder", "Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(true);
                _createOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                Log.d("CreateOrder", "Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun updateOrder(accessToken: String, orderBody: UpdateOrderBody, onResult: (Boolean) -> Unit) {
        _updateOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            val orderBodyJson = Gson().toJson(orderBody)
            Log.d("UpdateOrder", "OrderBody JSON: $orderBodyJson") // Log the JSON payload
            val orderBodyRequest = orderBodyJson.toRequestBody("application/json".toMediaTypeOrNull())

            try {
                val response = orderAPI.updateOrder("Bearer $accessToken", orderBody)
                Log.d("UpdateOrder", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _updateOrderResponse.value = NetworkResponse.Success(it)
                            onResult(true)
                        } else {
                            _updateOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            onResult(false)
                        }
                    }
                } else {
                    _updateOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                _updateOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                onResult(false)
            }
        }
    }

    fun updateOrderStatus(accessToken: String, orderID: Int, status: String, onResult: (Boolean) -> Unit) {
        _updateOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.updateOrderStatus("Bearer $accessToken", orderID, status)
                Log.d("UpdateOrderStatus", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _updateOrderResponse.value = NetworkResponse.Success(it)
                            onResult(true)
                        } else {
                            _updateOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            onResult(false)
                        }
                    }
                } else {
                    _updateOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.d("UpdateOrderStatus", "Something went wrong: ${e.message.toString()}")
                _updateOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                onResult(false)
            }
        }
    }

    fun setPaymentMethod(accessToken: String, orderID: Int, paymentMethod: String, onResult: (Boolean) -> Unit) {
        _updateOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.setPaymentMethod("Bearer $accessToken", orderID, paymentMethod)
                Log.d("SetPaymentMethod", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _updateOrderResponse.value = NetworkResponse.Success(it)
                            onResult(true)
                        } else {
                            _updateOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            onResult(false)
                        }
                    }
                } else {
                    _updateOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.d("SetPaymentMethod", "Something went wrong: ${e.message.toString()}")
                _updateOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
                onResult(false)
            }
        }
    }

    fun getOrder(accessToken: String, orderID: Int) {
        _getOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.getOrder("Bearer $accessToken", orderID)
                Log.d("GetOrder", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _getOrderResponse.value = NetworkResponse.Success(it)
                        } else {
                            _getOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                        }
                    }
                } else {
                    _getOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _getOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun sendMessage(accessToken: String, comment: String, orderID: Int, onResult: (Boolean) -> Unit) {
        _getMessageResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val commentBody = CommentBody(
                    OrderID = orderID,
                    sender = "u", // sender is always the user
                    comments = comment
                )

                val commentBodyJson = Gson().toJson(commentBody)
                Log.d("SendMessage", "CommentBody JSON: $commentBodyJson") // Log the JSON payload
                val response = orderAPI.sendMessage("Bearer $accessToken", commentBody)
                Log.d("SendMessage", "Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _getMessageResponse.value = NetworkResponse.Success(it)
                            onResult(true)
                        } else {
                            _getMessageResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                            onResult(false)
                        }
                    }
                } else {
                    _getMessageResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("SendMessage", "Something went wrong: ${e.message.toString()}")
            }
        }
    }
}