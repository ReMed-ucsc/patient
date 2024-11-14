package com.example.remed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ViewOrderScreen(navController: NavController, orderId: String) {
    val orderStatus = "Processing" // Example status
    val pharmacyMessage = "Your order is being prepared and will be dispatched soon."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp)
    ) {
        Text(
            text = "Order Details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Order ID: $orderId",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Pharmacy: Pharmacy A",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Medicines: Paracetamol, Ibuprofen",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Total Price: $20",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Order Status Section
        Text(
            text = "Order Status",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = orderStatus,
                fontSize = 18.sp,
                color = when (orderStatus) {
                    "Processing" -> Color.Blue
                    "Waiting for Accept" -> Color.Yellow
                    "On Delivery" -> Color.Green
                    "Completed" -> Color.Gray
                    "Cancelled" -> Color.Red
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )
        }

        // Pharmacy Message Section
        Text(
            text = "Message from Pharmacy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = pharmacyMessage,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}