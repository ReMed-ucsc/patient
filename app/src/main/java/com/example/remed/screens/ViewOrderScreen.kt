// ViewOrderScreen.kt
package com.example.remed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.Comment
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@Composable
fun ViewOrderScreen(navController: NavController, orderId: String, viewModel: OrderViewModel) {
    val orderResponse by viewModel.getOrderResponse.observeAsState()
    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }
    var newMessage by remember { mutableStateOf("") }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) {
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
    }

    accessToken?.let {
        LaunchedEffect(orderId) {
            viewModel.getOrder(it, orderId.toInt())
        }
    }

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

        when (val result = orderResponse) {
            is NetworkResponse.Success -> {
                val order = result.data.data.orderDetails
                val products = result.data.data.productDetails
//                lets add dummy messages for now
                val messages = listOf(
                    Comment("Pharmacy", "Your order is being processed"),
                    Comment("User", "Okay, thank you")
                )
//                val messages = result.data.data.messages

                Text(
                    text = "Order ID: ${order.OrderID}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Pharmacy: ${order.PharmacyID}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Display medicines in a row
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Medicines:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    products.forEach { product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = product.ProductName,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Qty: ${1}",
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

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
                        text = order.status,
                        fontSize = 18.sp,
                        color = when (order.status) {
                            "PROCESSING" -> Color.Blue
                            "WAITING" -> Color.Yellow
                            "USER_PICKED_UP" -> Color.Green
                            "DELIVERED" -> Color.Gray
                            "REJECTED" -> Color.Red
                            "CANCELLED" -> Color.Magenta
                            "DELIVERY_FAILED" -> Color.Cyan
                            else -> Color.Black
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                // Messages Section
                Text(
                    text = "Messages",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    messages.forEach { message ->
                        Text(
                            text = "${message.sender}: ${message.comments}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // New Message Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    IconButton(
                        onClick = {
                            // Handle sending new message
//                            if (newMessage.isNotEmpty()) {
//                                viewModel.sendMessage(order.OrderID, newMessage)
//                                newMessage = ""
//                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message")
                    }
                }
            }
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            is NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            null -> {}
        }
    }
}