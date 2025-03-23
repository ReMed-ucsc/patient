package com.example.remed.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.api.order.MedicineProduct
import com.example.remed.api.order.PharmacyData
import com.example.remed.navigation.HomeRouteScreens
import com.google.gson.Gson

@Composable
fun PharmacyWithMedicineCard(
    pharmacy: PharmacyData,
    navController: NavController,
    selectedMedicines: List<MedicineProduct>
) {
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
            Spacer(modifier = Modifier.height(4.dp))

            if(pharmacy.notAvailableMedicineCount?.toInt() == 0) {
                Text(
                    text = "All medicines are available",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }else {
//                print not available medicine list using pharmacy.notAvailableProducts

                Text(
                    text = "Not available product: ${pharmacy.notAvailableProducts}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
//  Serialize the list of MedicineProduct objects to a JSON string and pass it as a navigation argument
                onClick = {
                    val gson = Gson()
                    val selectedMedicinesJson = gson.toJson(selectedMedicines)
                    val pharmacyJson = gson.toJson(pharmacy)
                    navController.navigate("${HomeRouteScreens.PlaceOrder.route}/$selectedMedicinesJson/$pharmacyJson")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Proceed to Order")
            }
        }
    }
}