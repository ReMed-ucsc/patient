package com.example.remed.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.remed.screens.History
import com.example.remed.screens.HomeScreen
import com.example.remed.screens.MainScreen
import com.example.remed.screens.SelectMedicinesScreen

fun NavGraphBuilder.appGraph(navController: NavController){
    navigation(startDestination = Screens.ScreenHomeRoute.route, route = Screens.AppRoute.route){
        composable(route = Screens.ScreenMainRoute.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screens.ScreenHomeRoute.route) {
            HomeScreen(navController = navController)
        }

        composable(route = Screens.ScreenSelectMedicineRoute.route) {
            SelectMedicinesScreen(navController = navController)
        }

        composable(route = Screens.ScreenHistoryRoute.route) {
            History()
        }



//        composable(route = Screens.ScreenProfileRoute.route) {
//            ScreenB(navController = navController)
//        }
//        composable(route = Screens.ScreenSettingsRoute.route) {
//            ScreenB(navController = navController)
//        }
    }
}