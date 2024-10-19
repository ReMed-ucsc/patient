package com.example.remed.navigation.graph

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.remed.MainActivity
import com.example.remed.models.AuthViewModel
import com.example.remed.navigation.AuthRouteScreen
import com.example.remed.navigation.Graph
import com.example.remed.screens.ForgetPassScreen
import com.example.remed.screens.LoginScreen
import com.example.remed.screens.RegisterScreen

fun NavGraphBuilder.authNavGraph(rootNavController: NavController){
//    val mainActivity = MainActivity()
//    val viewModelProvider = ViewModelProvider(mainActivity)
//    val loginViewModel = viewModelProvider[LoginViewModel::class.java]
//    val registerViewModel = viewModelProvider[RegisterViewModel::class.java]

    navigation(
        route = Graph.AUTH,
        startDestination = AuthRouteScreen.Login.route
    ){
        composable(route = AuthRouteScreen.Login.route) {
            val loginViewModel: AuthViewModel = viewModel() // Use viewModel() here
            LoginScreen(viewModel = loginViewModel, navController = rootNavController)
        }
        composable(route = AuthRouteScreen.Register.route) {
            val registerViewModel: AuthViewModel = viewModel() // Use viewModel() here
            RegisterScreen(viewModel = registerViewModel, navController = rootNavController)
        }
        composable(route = AuthRouteScreen.ForgetPassword.route) {
            ForgetPassScreen(navController = rootNavController)
        }
    }
}