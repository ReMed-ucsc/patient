package com.example.remed.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remed.navigation.Graph
import com.example.remed.screens.MainScreen

@Composable
fun RootNavGraph(){
    val rootNavController = rememberNavController()
    NavHost(
        navController = rootNavController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
    ){
        authNavGraph(rootNavController = rootNavController)
        composable(route = Graph.MAIN){
            MainScreen(rootNavHostController = rootNavController)
        }

        homeNavGraph(navController = rootNavController)
    }

}