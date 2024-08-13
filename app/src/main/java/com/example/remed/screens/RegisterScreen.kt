package com.example.remed.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.R
import com.example.remed.api.NetworkResponse
import com.example.remed.models.RegisterViewModel
import com.example.remed.navigation.AuthRouteScreen
import com.example.remed.navigation.Graph
import kotlinx.coroutines.delay
import java.util.regex.Pattern


@Composable
fun  RegisterScreen(navController: NavController, viewModel: RegisterViewModel, modifier: Modifier = Modifier){
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val registerResponse = viewModel.registerResponse.observeAsState()

    fun validateName(): Boolean {
        return if (name.isEmpty()) {
            nameError = "Name cannot be empty"
            false
        } else {
            nameError = null
            true
        }
    }

    fun validateEmail(): Boolean {
        return if (email.isEmpty()) {
            emailError = "Email cannot be empty"
            false
        } else if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            emailError = "Invalid email address"
            false
        } else {
            emailError = null
            true
        }
    }

    fun validatePassword(): Boolean {
        return if (password.isEmpty()) {
            passwordError = "Password cannot be empty"
            false
        } else if (password.length < 8) {
            passwordError = "Password must be at least 8 characters long"
            false
        } else if (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
            passwordError = "Password must contain at least one uppercase letter"
            false
        } else if (!Pattern.compile(".*[a-z].*").matcher(password).matches()) {
            passwordError = "Password must contain at least one lowercase letter"
            false
        } else if (!Pattern.compile(".*[0-9].*").matcher(password).matches()) {
            passwordError = "Password must contain at least one number"
            false
        } else {
            passwordError = null
            true
        }
    }

    fun validateConfirmPassword(): Boolean {
        return if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Confirm password cannot be empty"
            false
        }else if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            false
        } else {
            confirmPasswordError = null
            true
        }
    }

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

        Text(text = "Join with us")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                validateName()
            },
            label = { Text(text = "Name") },
            isError = nameError != null
        )
        if (nameError != null) {
            Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validateEmail()
            },
            label = { Text(text = "Email address") },
            isError = emailError != null
        )
        if (emailError != null) {
            Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                validatePassword()
            },
            label = { Text(text = "Password") },
            isError = passwordError != null,
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
        if (passwordError != null) {
            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                validateConfirmPassword()
            },
            label = { Text(text = "Confirm Password") },
            isError = confirmPasswordError != null,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                }
            }
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val isNameValid = validateName()
                val isEmailValid = validateEmail()
                val isPasswordValid = validatePassword()
                val isConfirmPasswordValid = validateConfirmPassword()

                if (isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
                    viewModel.register(name, email, password)
                }

            }
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(32.dp))


        Button(onClick = {
            navController.navigate(AuthRouteScreen.Login.route)
        }) {
            Text(text = "Already has an account.")
        }

        when (val result = registerResponse.value) {
            is NetworkResponse.Success -> {
                LaunchedEffect(key1 = result.data) {
                    delay(1000L)
                    navController.navigate(AuthRouteScreen.Login.route) {
                        popUpTo(Graph.AUTH) { inclusive = true}
                    }
                }
                    Text(text = result.data.message)
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