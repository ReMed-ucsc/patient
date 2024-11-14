package com.example.remed.navigation.graph

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.remed.models.OrderViewModel
import com.example.remed.navigation.Graph
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.navigation.MainRouteScreens
import com.example.remed.screens.OrderScreen
import com.example.remed.screens.SearchMedicineScreen
import com.example.remed.screens.SearchPharmacyScreen
import com.example.remed.screens.SelectMedicinesScreen
import com.example.remed.screens.History
import com.example.remed.screens.ViewOrderScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    navigation(
        startDestination = HomeRouteScreens.SearchMedicine.route,
        route = Graph.HOME
    ){
        composable(route = HomeRouteScreens.SelectMedicine.route) {
            SelectMedicinesScreen(navController = navController)
        }

        composable(route = HomeRouteScreens.SearchMedicine.route) {
            SearchMedicineScreen(navController = navController)
        }

        composable(route = HomeRouteScreens.SearchPharmacy.route) {
            val orderViewModel: OrderViewModel = viewModel()
            SearchPharmacyScreen(navController = navController, viewModel = orderViewModel)
        }

        composable(route = "${HomeRouteScreens.PlaceOrder.route}/{selectedMedicines}") { backStackEntry ->
            OrderScreen(navController = navController, backStackEntry = backStackEntry)
        }

        composable(route = MainRouteScreens.History.route) {
            val orderViewModel: OrderViewModel = viewModel()
            History(navController = navController, viewModel = orderViewModel)
        }

        composable(route = "${HomeRouteScreens.ViewOrder.route}/{orderId}") { backStackEntry ->
            ViewOrderScreen(navController = navController, orderId = backStackEntry.arguments?.getString("orderId") ?: "")
        }
    }
}