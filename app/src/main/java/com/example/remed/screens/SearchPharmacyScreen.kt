package com.example.remed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.remed.navigation.HomeRouteScreens

@Composable
fun SearchPharmacyScreen(navController: NavController) {
    var location by remember { mutableStateOf(TextFieldValue("")) }
    var pharmacies by remember { mutableStateOf(listOf<Pharmacy>()) }
    var showPharmacyList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical =  40.dp)
    ) {
        // Location search bar
        Text(
            text = "Enter Location",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = location,
            onValueChange = { location = it },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.LightGray)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Set Location Button
        Button(
            onClick = {
                // Simulate fetching nearby pharmacies
                pharmacies = listOf(
                    Pharmacy("Pharmacy A", "123 Main St, City", "123-456-7890"),
                    Pharmacy("Pharmacy B", "456 Elm St, City", "987-654-3210"),
                    Pharmacy("Pharmacy C", "789 Oak St, City", "555-555-5555"),
                )
                showPharmacyList = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Set Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nearby Pharmacies List using LazyColumn
        if (showPharmacyList) {
            Text(
                text = "Nearby Pharmacies",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(pharmacies) { pharmacy ->
                    PharmacyCard(pharmacy, navController)
                }
            }
        }
    }
}

@Composable
fun PharmacyCard(pharmacy: Pharmacy, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = pharmacy.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Address: ${pharmacy.address}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contact: ${pharmacy.contact}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(HomeRouteScreens.PlaceOrder.route)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Place Order")
            }
        }
    }
}

data class Pharmacy(val name: String, val address: String, val contact: String)

//@Preview(showBackground = true)
//@Composable
//fun SearchPharmacyScreenPreview() {
//    SearchPharmacyScreen()
//}
