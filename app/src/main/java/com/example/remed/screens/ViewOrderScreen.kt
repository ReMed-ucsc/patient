// ViewOrderScreen.kt
package com.example.remed.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.Comment
import com.example.remed.api.order.MedicineProduct
import com.example.remed.api.order.OrderBody
import com.example.remed.api.order.UpdateOrderBody
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import com.example.remed.navigation.MainRouteScreens
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ViewOrderScreen(navController: NavController, orderId: String, viewModel: OrderViewModel) {
    val orderResponse by viewModel.getOrderResponse.observeAsState()
    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    var newMessage by remember { mutableStateOf("") }
    var showMessages by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showMedicineDialog by remember { mutableStateOf(false) }

    var medicines by remember { mutableStateOf(listOf<MedicineProduct>()) }
    var quantities by remember { mutableStateOf(mutableListOf<MutableState<Int>>()) }
    var removedMedicines by remember { mutableStateOf(listOf<Int>()) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) {
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
    }

    accessToken?.let {
        LaunchedEffect(orderId) {
            viewModel.getOrder(it, orderId.toInt())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp)
    ) {
        item {
            Text(
                text = "Order Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Log.d("ViewOrderScreen", "OrderResponse: $orderResponse")
        when (val result = orderResponse) {
//            checking both network response success and orderResponse result errors
            is NetworkResponse.Success -> {
                val order = result.data.data.orderDetails
                val products = result.data.data.productDetails
                val messages = result.data.data.comments


                if (!isEditing) {
                    medicines = products
                    quantities = products.map { mutableStateOf(it.Quantity) }.toMutableList()
                }

                item {
                    Text(
                        text = "Order ID: ${order.OrderID}",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Pharmacy: ${order.pharmacyName}",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!medicines.isEmpty()) {
                    item {
                        Text(
                            text = "Medicines:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    if (isEditing) {
                        items(medicines.size) { index ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = medicines[index].ProductName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { if (quantities[index].value > 1) quantities[index].value-- }) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease Quantity"
                                    )
                                }
                                Text(
                                    text = quantities[index].value.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                IconButton(onClick = { quantities[index].value++ }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase Quantity"
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = {
                                    removedMedicines = removedMedicines + medicines[index].ProductID
                                    medicines = medicines - medicines[index]
                                    quantities.removeAt(index)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Medicine",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    showMedicineDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Add Medicine")
                            }
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, Color.Black)
                            ) {
                                Text(
                                    text = "Medicine Name",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(2f).padding(8.dp)
                                )
                                Text(
                                    text = "Qty",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f).padding(8.dp)
                                )
                                Text(
                                    text = "Price",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f).padding(8.dp)
                                )
                            }
                        }

                        items(products) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, Color.Black)
                            ) {
                                Text(
                                    text = product.ProductName,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(2f).padding(8.dp)
                                )
                                Text(
                                    text = "${product.Quantity}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f).padding(8.dp)
                                )
                                Text(
                                    text = "${product.UnitPrice}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f).padding(8.dp)
                                )
                            }
                        }
                    }

                    item {
                        val totalPrice = products.sumOf { it.UnitPrice * it.Quantity }
                        Text(
                            text = "Total: $totalPrice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (order.status == "WAITING") {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (isEditing) {
                                        val productIDs = medicines.map { it.ProductID }
                                        val quantities = quantities.map { it.value }

                                        val orderBody = UpdateOrderBody(
                                            orderID = order.OrderID,
                                            productIDs = productIDs,
                                            quantities = quantities,
                                            removedProductIDs = removedMedicines
                                        )

                                        Log.d("OrderScreen", "OrderBody: $orderBody")

                                        accessToken?.let {
                                            viewModel.updateOrder(it, orderBody) { success ->
                                                if (success) {
                                                    Toast.makeText(
                                                        context,
                                                        "Order updated",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.getOrder(
                                                        it,
                                                        orderId.toInt()
                                                    ) // Reload the screen
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to update order",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                    isEditing = !isEditing
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = if (isEditing) "Save Changes" else "Edit Order")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

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
                }

                item {
                    Button(
                        onClick = { showMessages = !showMessages },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (showMessages) "Hide Messages" else "See Messages")
                    }
                }

                if (showMessages) {
                    item {
                        Text(
                            text = "Messages",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )

                        if (messages.isEmpty()) {
                            Text(
                                text = "No messages yet",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                messages.reversed().forEach { message ->
                                    val isUserMessage = message.sender == "user"
                                    val bubbleColor = if (isUserMessage) Color(0xFFDCF8C6) else Color(0xFFECECEC)
                                    val alignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
                                    val alignmentH = if (isUserMessage) Alignment.End else Alignment.Start

                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .align(alignmentH)
                                        ,
                                        contentAlignment = alignment
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(bubbleColor, shape = RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalAlignment = alignmentH
                                        ) {
                                            Text(
                                                text = message.comments,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = message.createdAt,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
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
                                Log.d("SendMessage", "Sending message: $newMessage")
                                if (newMessage.isNotEmpty()) {
                                    accessToken?.let { token ->
                                        viewModel.sendMessage(token, newMessage, orderId.toInt()) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show()
                                                viewModel.getOrder(token, orderId.toInt()) // Reload the screen
                                                newMessage = ""
                                            } else {
                                                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message")
                        }
                    }
                }

                item {
                    order.prescription?.let { url ->
                        Text(
                            text = "Prescription Image",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Prescription Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(16.dp)
                                .clickable {
                                    imageUrl = url
                                    showDialog = true
                                }
                        )
                    }
                }
            }
            is NetworkResponse.Error -> {
                item {
                    Text(text = result.message)
                }
            }
            is NetworkResponse.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            null -> {}
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Zoomed Prescription Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }

    if (showMedicineDialog) {
        MedicineSelectionDialog(
            onDismiss = { showMedicineDialog = false },
            onMedicineSelected = { medicine ->
                medicines = medicines + medicine
                quantities.add(mutableStateOf(1))
                showMedicineDialog = false
            },
            selectedMedicines = medicines
        )
    }
}