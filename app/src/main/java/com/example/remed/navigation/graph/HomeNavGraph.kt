package com.example.remed.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.remed.navigation.Graph
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.screens.SelectMedicinesScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    navigation(
        startDestination = HomeRouteScreens.SelectMedicine.route,
        route = Graph.HOME
    ){
        composable(route = HomeRouteScreens.SelectMedicine.route
        ) {
            SelectMedicinesScreen(navController = navController)
        }
    }
}