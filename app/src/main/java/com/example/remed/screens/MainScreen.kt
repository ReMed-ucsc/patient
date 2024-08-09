package com.example.remed.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.remed.navigation.NavItem
import com.example.remed.navigation.Screens
@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavController) {
    val navItemList = listOf(
        NavItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = Screens.ScreenHomeRoute.route
        ),
        NavItem(
            label = "Reminder",
            icon = Icons.Default.Timelapse,
            route = Screens.ScreenReminderRoute.route // Update to the correct route
        ),
        NavItem(
            label = "History",
            icon = Icons.Default.History,
            route = Screens.ScreenHistoryRoute.route // Update to the correct route
        ),
        NavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            route = Screens.ScreenProfileRoute.route
        )
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(text = navItem.label) },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController
        )
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavController) {
    when (selectedIndex) {
        0 -> HomeScreen(navController = navController)
        1 -> History()
        2 -> History()
        3 -> ProfileScreen()
    }
}
