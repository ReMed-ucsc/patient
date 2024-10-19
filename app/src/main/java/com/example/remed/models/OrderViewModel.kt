package com.example.remed.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.order.OrderList
import com.example.remed.api.order.PharmacyList
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderAPI = RetrofitInstance.orderAPI

    private val _ordersResponse = MutableLiveData<NetworkResponse<OrderList>>()
    val ordersResponse: LiveData<NetworkResponse<OrderList>> = _ordersResponse

    private val _pharmacyListResponse = MutableLiveData<NetworkResponse<PharmacyList>>()
    val pharmacyListResponse: LiveData<NetworkResponse<PharmacyList>> = _pharmacyListResponse

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
//                        no errors while login
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
}