package com.example.remed.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.remed.models.LoginViewModel
import com.example.remed.navigation.AuthRouteScreen
import com.example.remed.navigation.Graph
import com.example.remed.screens.ForgetPassScreen
import com.example.remed.screens.LoginScreen
import com.example.remed.screens.RegisterScreen

fun NavGraphBuilder.authNavGraph(viewModel: LoginViewModel, rootNavController: NavController){
    navigation(
        route = Graph.AUTH,
        startDestination = AuthRouteScreen.Login.route
    ){
        composable(route = AuthRouteScreen.Login.route) {
            LoginScreen(viewModel = LoginViewModel(), navController = rootNavController)
        }
        composable(route = AuthRouteScreen.Register.route) {
            RegisterScreen(navController = rootNavController)
        }
        composable(route = AuthRouteScreen.ForgetPassword.route) {
            ForgetPassScreen(navController = rootNavController)
        }
    }
}