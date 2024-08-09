package com.example.remed.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.remed.models.LoginViewModel

@Composable
fun Nav(viewModel: LoginViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.AuthRoute.route){
        authGraph(viewModel = LoginViewModel(), navController = navController)
        appGraph(navController = navController)

    }

}