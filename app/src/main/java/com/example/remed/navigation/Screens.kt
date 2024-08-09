package com.example.remed.navigation

sealed class Screens(val route : String) {
    object ScreenLoginRoute : Screens(route = "Login")
    object ScreenForgetPassRoute : Screens(route = "Forget")
    object ScreenRegisterRoute : Screens(route = "Register")

    object ScreenMainRoute : Screens(route = "Main")
    object ScreenHomeRoute : Screens(route = "Home")
    object ScreenSelectMedicineRoute : Screens(route = "SelectMedicine")
    object ScreenReminderRoute : Screens(route = "Reminder")
    object ScreenHistoryRoute : Screens(route = "History")
    object ScreenProfileRoute : Screens(route = "Profile")
    object ScreenSettingsRoute : Screens(route = "Settings")


    object AuthRoute : Screens(route = "Auth")
    object AppRoute : Screens(route = "App")

}