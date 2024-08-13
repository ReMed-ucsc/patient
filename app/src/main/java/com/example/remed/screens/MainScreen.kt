package com.example.remed.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.remed.navigation.MainRouteScreens
import com.example.remed.navigation.NavItem
import com.example.remed.navigation.graph.MainNavGraph

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    rootNavHostController: NavHostController,
    homeNavController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry) {
        derivedStateOf {
            navBackStackEntry?.destination?.route
        }
    }
    val navItemList = listOf(
        NavItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = MainRouteScreens.Home.route
        ),
        NavItem(
            label = "Reminder",
            icon = Icons.Default.Timelapse,
            route = MainRouteScreens.Reminder.route
        ),
        NavItem(
            label = "History",
            icon = Icons.Default.History,
            route = MainRouteScreens.History.route
        ),
        NavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            route = MainRouteScreens.Profile.route
        )
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            homeNavController.navigate(navItem.route){
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                homeNavController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                    // Pop up to the start destination, clearing the back stack
                                    popUpTo(startDestinationRoute) {
                                        // Save the state of popped destinations
                                        saveState = true
                                    }
                                }

                                // Configure navigation to avoid multiple instances of the same destination
                                launchSingleTop = true

                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }

                        },
                        label = { Text(text = navItem.label) },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) }
                    )
                }
            }
        }
    ) {
        MainNavGraph(
            rootNavController = rootNavHostController,
            homeNavController = homeNavController
        )
    }

}

//@Composable
//fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavController) {
//    when (selectedIndex) {
//        0 -> HomeScreen(navController = navController)
//        1 -> History()
//        2 -> History()
//        3 -> ProfileScreen()
//        else -> HomeScreen(navController = navController)
//    }
//
//}

//{ innerPadding -> // Add content block here
//    ContentScreen( // Call ContentScreen to display content
//        modifier = modifier.padding(innerPadding),
//        selectedIndex = selectedIndex,
//        navController = navController
//    )
//    navController.addOnDestinationChangedListener { _, destination, _ ->
//        selectedIndex = when(destination.route){
//            Screens.ScreenHomeRoute.route -> 0
//            Screens.ScreenReminderRoute.route -> 1
//            Screens.ScreenHistoryRoute.route -> 2
//            Screens.ScreenProfileRoute.route -> 3
//            else -> 0
//        }
//    }
//}

//Scaffold(
//bottomBar = {
//    NavigationBar {
//        navItemList.forEachIndexed { index, navItem ->
//            NavigationBarItem(
//                selected = selectedIndex == index,
//                onClick = {
//                    selectedIndex = index
//                    navController.navigate(navItem.route){
//                        popUpTo(navController.graph.findStartDestination().id){
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//
//                },
//                label = { Text(text = navItem.label) },
//                icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) }
//            )
//        }
//    }
//}
//) {
//    navController.addOnDestinationChangedListener { _, destination, _ ->
//        selectedIndex = when (destination.route) {
//            Screens.ScreenHomeRoute.route -> 0
//            Screens.ScreenReminderRoute.route -> 1
//            Screens.ScreenHistoryRoute.route -> 2
//            Screens.ScreenProfileRoute.route -> 3
//            else -> 0
//        }
//    }
//}
