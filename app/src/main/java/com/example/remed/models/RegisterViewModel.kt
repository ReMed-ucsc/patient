package com.example.remed.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.register.RegisterModel
import com.example.remed.api.register.Request
import com.example.remed.api.register.Result
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val registerApi = RetrofitInstance.registerApi
    private val _registerResponse = MutableLiveData<NetworkResponse<Result>>()
    val registerResponse: LiveData<NetworkResponse<Result>> = _registerResponse

    fun register(name: String, email: String, password: String) {
        _registerResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", "Starting register request")
                val response = registerApi.register(Request(name, email, password))
                Log.d("RegisterViewModel", "Received response: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
//                        no errors while login
                        if (!it.error) {
                            _registerResponse.value = NetworkResponse.Success(it)
                        } else {
                            _registerResponse.value = NetworkResponse.Error("Login failed: ${it.message }")
                        }
                    }
                } else {
                    _registerResponse.value = NetworkResponse.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }
}