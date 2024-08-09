package com.example.remed.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remed.models.LoginViewModel
import com.example.remed.navigation.Graph
import com.example.remed.navigation.Screens
import com.example.remed.navigation.appGraph
import com.example.remed.navigation.authGraph
import com.example.remed.screens.MainScreen

@Composable
fun RootNavGraph(viewModel: LoginViewModel){
    val rootNavController = rememberNavController()
    NavHost(
        navController = rootNavController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
    ){
        authNavGraph(viewModel = LoginViewModel(), rootNavController = rootNavController)
        composable(route = Graph.MAIN){
            MainScreen(rootNavHostController = rootNavController)
        }

        homeNavGraph(navController = rootNavController)
    }

}