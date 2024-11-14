package com.example.remed.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remed.api.NetworkResponse
import com.example.remed.api.RetrofitInstance
import com.example.remed.api.auth.AuthResult
import com.example.remed.api.auth.LoginRequest
import com.example.remed.api.auth.RegisterRequest
import com.example.remed.api.auth.LoginModel
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authAPI = RetrofitInstance.authAPI


    //    private variable to observe state of register
    private val _registerResponse = MutableLiveData<NetworkResponse<AuthResult>>()

    //    public variable to expose to UI
    val registerResponse: LiveData<NetworkResponse<AuthResult>> = _registerResponse

    private val _loginResponse = MutableLiveData<NetworkResponse<LoginModel>>()
    val loginResponse: LiveData<NetworkResponse<LoginModel>> = _loginResponse

    fun login(email: String, password: String) {
        _loginResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = authAPI.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginModel = response.body()
                    if (loginModel != null) {
                        val authResult = loginModel.result
                        if (authResult != null) {
                            if (!authResult.error) {
                                _loginResponse.value = NetworkResponse.Success(loginModel)
                            } else {
                                _loginResponse.value = NetworkResponse.Error("Login failed: ${authResult.message}")
                            }
                        } else {
                            _loginResponse.value = NetworkResponse.Error("AuthResult is null")
                        }
                    } else {
                        _loginResponse.value = NetworkResponse.Error("LoginModel is null")
                    }
                } else {
                    _loginResponse.value = NetworkResponse.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerResponse.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = authAPI.register(RegisterRequest(name, email, password))
                if (response.isSuccessful) {
                    response.body()?.let {
//                        no errors while login
                        if (!it.error) {
                            _registerResponse.value = NetworkResponse.Success(it)
                        } else {
                            _registerResponse.value = NetworkResponse.Error("Register failed: ${it.message }")
                        }
                    }
                } else {
                    _registerResponse.value = NetworkResponse.Error("Register failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerResponse.value = NetworkResponse.Error("Something went wrong: ${e.message.toString()}")
            }
        }
    }
}
