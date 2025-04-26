// HistoryScreen.kt
package com.example.remed.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
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
import com.example.remed.api.order.OrderData
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import com.example.remed.navigation.HomeRouteScreens
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun History(navController: NavController, viewModel: OrderViewModel) {
    val ordersResponse = viewModel.ordersResponse.observeAsState()

    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    // Filter state variables
    var showFilters by remember { mutableStateOf(false) }
    var statusFilter by remember { mutableStateOf("All") }
    var dateFilter by remember { mutableStateOf("All") }

    // Status options
    val statusOptions = listOf("All", "WAITING", "PROCESSING", "ACCEPT QUOTATION", "ACCEPTED", "REJECTED")
    var expandedStatusDropdown by remember { mutableStateOf(false) }

    // Date filter options
    val dateOptions = listOf("All", "Today", "This Week", "This Month", "Last Month")
    var expandedDateDropdown by remember { mutableStateOf(false) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) {
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
        if (accessToken != null) {
            viewModel.getOrders(accessToken!!)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
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

            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(visible = showFilters) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter Orders",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Status Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status:",
                        modifier = Modifier.width(80.dp)
                    )

                    Box {
                        OutlinedButton(
                            onClick = { expandedStatusDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(statusFilter)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Status"
                            )
                        }

                        DropdownMenu(
                            expanded = expandedStatusDropdown,
                            onDismissRequest = { expandedStatusDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        statusFilter = option
                                        expandedStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date:",
                        modifier = Modifier.width(80.dp)
                    )

                    Box {
                        OutlinedButton(
                            onClick = { expandedDateDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(dateFilter)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Date Range"
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDateDropdown,
                            onDismissRequest = { expandedDateDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            dateOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        dateFilter = option
                                        expandedDateDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        when (val result = ordersResponse.value) {
            is NetworkResponse.Success -> {
                val allOrders: List<OrderData> = result.data.data

                // Apply filters
                val filteredOrders = allOrders.filter { order ->
                    // Status filter
                    val statusMatched = if (statusFilter == "All") true else order.status == statusFilter

                    // Date filter
                    val dateMatched = when (dateFilter) {
                        "All" -> true
                        "Today" -> isToday(order.date)
                        "This Week" -> isThisWeek(order.date)
                        "This Month" -> isThisMonth(order.date)
                        "Last Month" -> isLastMonth(order.date)
                        else -> true
                    }

                    statusMatched && dateMatched
                }

                if (filteredOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders match your filters", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(MaterialTheme.colorScheme.primary)
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

                        items(filteredOrders) { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        navController.navigate("${HomeRouteScreens.ViewOrder.route}/${order.OrderID}")
                                    }
                                    .background(
                                        color = getStatusColor(order.status).copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.OrderID.toString(),
                                    modifier = Modifier.weight(1f)
                                )

                                Chip(
                                    onClick = { },
                                    colors = ChipDefaults.chipColors(
                                        backgroundColor = getStatusColor(order.status).copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.weight(2f)
                                ) {
                                    Text(
                                        text = order.status,
                                        color = getStatusColor(order.status)
                                    )
                                }

                                Text(
                                    text = formatDate(order.date),
                                    modifier = Modifier.weight(2f)
                                )
                            }

                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
            is NetworkResponse.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error loading orders", color = Color.Red)
                        Text(text = result.message, color = Color.Gray, fontSize = 14.sp)
                        Button(
                            onClick = {
                                accessToken?.let { viewModel.getOrders(it) }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            is NetworkResponse.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available")
                }
            }
        }
    }
}

// Helper functions for date filtering
fun isToday(dateString: String): Boolean {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val orderDate = format.parse(dateString)
        val today = Calendar.getInstance()
        val orderCal = Calendar.getInstance()
        orderCal.time = orderDate ?: return false

        return orderCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                orderCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        return false
    }
}

fun isThisWeek(dateString: String): Boolean {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val orderDate = format.parse(dateString)
        val today = Calendar.getInstance()
        val orderCal = Calendar.getInstance()
        orderCal.time = orderDate ?: return false

        return orderCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                orderCal.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)
    } catch (e: Exception) {
        return false
    }
}

fun isThisMonth(dateString: String): Boolean {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val orderDate = format.parse(dateString)
        val today = Calendar.getInstance()
        val orderCal = Calendar.getInstance()
        orderCal.time = orderDate ?: return false

        return orderCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                orderCal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
    } catch (e: Exception) {
        return false
    }
}

fun isLastMonth(dateString: String): Boolean {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val orderDate = format.parse(dateString)
        val today = Calendar.getInstance()
        val lastMonth = Calendar.getInstance()
        lastMonth.add(Calendar.MONTH, -1)
        val orderCal = Calendar.getInstance()
        orderCal.time = orderDate ?: return false

        return orderCal.get(Calendar.YEAR) == lastMonth.get(Calendar.YEAR) &&
                orderCal.get(Calendar.MONTH) == lastMonth.get(Calendar.MONTH)
    } catch (e: Exception) {
        return false
    }
}

fun formatDate(dateString: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return dateString
        return outputFormat.format(date)
    } catch (e: Exception) {
        return dateString
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "WAITING" -> Color(0xFFFF9800)  // Orange
        "PROCESSING" -> Color(0xFF2196F3)  // Blue
        "ACCEPT QUOTATION" -> Color(0xFF9C27B0)  // Purple
        "ACCEPTED" -> Color(0xFF4CAF50)  // Green
        "REJECTED" -> Color(0xFFF44336)  // Red
        else -> Color.Gray
    }
}