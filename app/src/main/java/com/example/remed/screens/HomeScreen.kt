package com.example.remed.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.navigation.Screens


@Composable
fun  HomeScreen(modifier: Modifier = Modifier, navController: NavController){
    Column(modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text= "Search for Medicine", fontSize = 32.sp)
        Spacer(modifier.height(40.dp))

//        set location
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Set Location")
        }
//        Get input of medication list from user
        Button(onClick = {
            navController.navigate(HomeRouteScreens.SelectMedicine.route)
        }) {
            Text(text = "Add Medication")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "View Medication list")
        }

        Spacer(modifier.height(32.dp))
        Button(onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Search available pharmacies")
        }


    }
}