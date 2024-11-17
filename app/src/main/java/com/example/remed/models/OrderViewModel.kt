package com.example.remed.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.order.CreateOrder
import com.example.remed.api.order.MedicineList
import com.example.remed.api.order.OrderBody
import com.example.remed.api.order.OrderList
import com.example.remed.api.order.OrderResult
import com.example.remed.api.order.PharmacyList
import kotlinx.coroutines.launch

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

    private val _createOrderResponse = MutableLiveData<NetworkResponse<CreateOrder>>()
    val createOrderResponse: LiveData<NetworkResponse<CreateOrder>> = _createOrderResponse

    private val _getOrderResponse = MutableLiveData<NetworkResponse<OrderResult>>()
    val getOrderResponse: LiveData<NetworkResponse<OrderResult>> = _getOrderResponse

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

    fun searchNearbyPharmacies(lat: Double = 6.84862699, long: Double = 79.924950) {
        _pharmacyListResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.searchNearbyPharmacies(lat, long)
                Log.d("SearchPharmacy", "Response Body: ${response.body()}")
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

    fun searchPharmacies(lat: Double = 6.84862699, long: Double = 79.924950, productIDs: List<Int>) {
        _pharmacyWithMedicineListResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.searchPharmacies(lat, long, productIDs)
                Log.d("SearchPharmacy", "Response Body: ${response.body()} productIDs: $productIDs")
                if (response.isSuccessful) {
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

    fun createOrder(accessToken: String, orderBody: OrderBody, onResult: (Boolean) -> Unit) {
        _createOrderResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = orderAPI.placeOrder("Bearer $accessToken", orderBody)
                onResult(response.isSuccessful)
                Log.d("CreateOrder", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.result.error) {
                            _createOrderResponse.value = NetworkResponse.Success(it)
                        } else {
                            _createOrderResponse.value = NetworkResponse.Error("Failed: ${it.result.message}")
                        }
                    }
                } else {
                    _createOrderResponse.value = NetworkResponse.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _createOrderResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
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
}