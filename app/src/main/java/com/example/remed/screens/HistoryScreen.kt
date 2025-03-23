// HistoryScreen.kt
package com.example.remed.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.OrderData
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import com.example.remed.navigation.AuthRouteScreen
import com.example.remed.navigation.Graph
import com.example.remed.navigation.HomeRouteScreens
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@Composable
fun History(navController: NavController, viewModel: OrderViewModel) {
    val ordersResponse = viewModel.ordersResponse.observeAsState()

    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) { // Use a key to prevent recomposition triggers
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
        if (accessToken != null) {
            viewModel.getOrders(accessToken!!)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .fillMaxHeight()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Order History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        when (val result = ordersResponse.value) {
            is NetworkResponse.Success -> {
                val orders : List<OrderData> = result.data.data

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(MaterialTheme.colors.onSecondary)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Order ID",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Status",
                                modifier = Modifier.weight(2f).padding(start = 16.dp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Date",
                                modifier = Modifier.weight(2f),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    items(orders) { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("${HomeRouteScreens.ViewOrder.route}/${order.OrderID}")
                                },
                            horizontalArrangement= Arrangement.SpaceBetween
                        ) {
                            Text(text = order.OrderID.toString(), modifier = Modifier.weight(1f).padding(start = 16.dp))
                            Text(text = order.status, modifier = Modifier.weight(2f)) // Using PharmacyID
                            Text(text = order.date, modifier = Modifier.weight(2f))
                        }
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