package com.example.remed.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.remed.models.AuthViewModel
import com.example.remed.models.OrderViewModel
import com.example.remed.models.ReminderViewModel
import com.example.remed.navigation.Graph
import com.example.remed.navigation.MainRouteScreens
import com.example.remed.screens.DashboardScreen
import com.example.remed.screens.History
import com.example.remed.screens.HomeScreen
import com.example.remed.screens.MainScreen
import com.example.remed.screens.ProfileScreen
import com.example.remed.screens.ReminderScreen

@Composable
fun MainNavGraph(
    rootNavController: NavHostController,
    homeNavController: NavHostController,

){
    NavHost(
        navController = homeNavController,
        route = Graph.MAIN,
        startDestination = MainRouteScreens.Home.route
    ){

        composable(route = MainRouteScreens.Home.route) {
            DashboardScreen(navController = rootNavController)
        }
        composable(route = MainRouteScreens.Reminder.route) {
            val reminderViewModel: ReminderViewModel = viewModel()
            ReminderScreen(navController = rootNavController, viewModel = reminderViewModel)
        }
        composable(route = MainRouteScreens.History.route) {
            val orderViewModel: OrderViewModel = viewModel()
            History(navController = rootNavController, viewModel = orderViewModel)
        }
        composable(route = MainRouteScreens.Profile.route) {
            ProfileScreen(navController = rootNavController)
        }
    }

}