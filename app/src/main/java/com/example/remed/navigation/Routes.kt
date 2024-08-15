package com.example.remed.navigation

object Graph{

    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
    const val HOME = "home_graph"
    const val ORDER = "order_graph"
    const val REMINDER = "reminder_graph"
}

sealed class AuthRouteScreen(val route : String){
    object Login : AuthRouteScreen(route = "LOGIN")
    object Register : AuthRouteScreen(route = "REGISTER")
    object ForgetPassword : AuthRouteScreen(route = "FORGET_PASSWORD")
}

sealed class MainRouteScreens(val route : String){
    object Home : MainRouteScreens(route = "Home")
    object Reminder : MainRouteScreens(route = "Reminder")
    object History : MainRouteScreens(route = "History")
    object Profile : MainRouteScreens(route = "Profile")
    object Settings : MainRouteScreens(route = "Settings")
}

sealed class HomeRouteScreens(val route : String){
    object SelectMedicine : HomeRouteScreens(route = "SelectMedicine")
    object SearchMedicine : HomeRouteScreens(route = "SearchMedicine")
    object SearchPharmacy : HomeRouteScreens(route = "SearchPharmacy")
    object PlaceOrder : HomeRouteScreens(route = "PlaceOrder")


}
