package com.example.remed.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.remed.models.LoginViewModel
import com.example.remed.screens.ForgetPassScreen
import com.example.remed.screens.LoginScreen
import com.example.remed.screens.RegisterScreen

fun NavGraphBuilder.authGraph(navController: NavController, viewModel: LoginViewModel){

    navigation(startDestination = Screens.ScreenLoginRoute.route, route = Screens.AuthRoute.route){
        composable(route = Screens.ScreenLoginRoute.route) {
            LoginScreen(viewModel = LoginViewModel(), navController = navController)
        }
        composable(route = Screens.ScreenRegisterRoute.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screens.ScreenForgetPassRoute.route) {
            ForgetPassScreen(navController = navController)
        }
    }
}