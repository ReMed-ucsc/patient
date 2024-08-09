package com.example.remed.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.login.LoginModel
import com.example.remed.api.login.Request
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val loginApi = RetrofitInstance.loginApi
    private val _loginResponse = MutableLiveData<NetworkResponse<LoginModel>>()
    val loginResponse: LiveData<NetworkResponse<LoginModel>> = _loginResponse

    fun login(email: String, password: String) {
        _loginResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting login request")
                val response = loginApi.login(Request(email, password))
                Log.d("LoginViewModel", "Received response: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
//                        no errors while login
                        if (!it.result.error) {
                            _loginResponse.value = NetworkResponse.Success(it)
                        } else {
                            _loginResponse.value = NetworkResponse.Error("Login failed: ${it.result.message }")
                        }
                    }
                } else {
                    _loginResponse.value = NetworkResponse.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }
}
