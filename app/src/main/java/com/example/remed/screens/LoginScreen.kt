package com.example.remed.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.R
import com.example.remed.api.NetworkResponse
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.AuthViewModel
import com.example.remed.navigation.AuthRouteScreen
import com.example.remed.navigation.Graph
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    val loginResponse = viewModel.loginResponse.observeAsState()

//    for storing access token
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Login Image",
            modifier = Modifier.size(200.dp)
        )
        Text(text = "ReMed", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = "Welcome Back")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text(text = "Email address") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                    viewModel.login(email, password)
            }
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Forgot Password", modifier = Modifier.clickable { })

        Spacer(modifier = Modifier.height(32.dp))


//        register
        Button(onClick = {
            navController.navigate(AuthRouteScreen.Register.route)
        }) {
            Text(text = "New to here? Register with us.")
        }

        LaunchedEffect(key1 = loginResponse.value) {
            if (loginResponse.value is NetworkResponse.Success) {
                val token = (loginResponse.value as NetworkResponse.Success).data.user.auth_token
                dataStore.saveAccessToken(token)

                // Fetch the token directly after saving
                accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
                Log.d("LoginScreen", "Retrieved Access Token:$accessToken") // Log
            }
        }

        when (val result = loginResponse.value) {
            is NetworkResponse.Success -> {
//                Log.d("AuthViewModel", "Login Response: ${result.data}")

                navController.navigate(Graph.MAIN){
                    popUpTo(AuthRouteScreen.Login.route){
                        inclusive = true
                    }
                }
            }
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            is NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            null -> {}
        }
    }
}
