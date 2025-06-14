// ViewOrderScreen.kt
package com.example.remed.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.Comment
import com.example.remed.api.order.MedicineProduct
import com.example.remed.api.order.UpdateOrderBody
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ViewOrderScreen(navController: NavController, orderId: String, viewModel: OrderViewModel) {
    val orderResponse by viewModel.getOrderResponse.observeAsState()
    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    var newMessage by remember { mutableStateOf("") }
    var showChatDialog by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showMedicineDialog by remember { mutableStateOf(false) }

    var medicines by remember { mutableStateOf(listOf<MedicineProduct>()) }
    var quantities by remember { mutableStateOf(mutableListOf<MutableState<Int>>()) }
    var removedMedicines by remember { mutableStateOf(listOf<Int>()) }

    var pharmacyID by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()

    // Extract messages from order response for chat
    val messages = remember { mutableStateOf<List<Comment>>(emptyList()) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) {
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
    }

    accessToken?.let {
        LaunchedEffect(orderId) {
            viewModel.getOrder(it, orderId.toInt())
        }
    }

    LaunchedEffect(orderResponse) {
        if (orderResponse is NetworkResponse.Success) {
            pharmacyID = (orderResponse as NetworkResponse.Success).data.data.orderDetails.PharmacyID
            messages.value = (orderResponse as NetworkResponse.Success).data.data.comments
        }
    }

    LaunchedEffect(medicines) {
        Log.d("CheckOverTheCounter", "Checking for over-the-counter medicines")

        if (medicines.isNotEmpty()) {
            val productIDs = medicines.map { it.ProductID }
            Log.d("CheckOverTheCounter", "API called with productIDs: $productIDs")
            viewModel.checkForOverTheCounter(productIDs)
        }
    }

    val checkForOverTheCounterResponse by viewModel.checkForOverTheCounterResponse.observeAsState()
    var isAllOverTheCounter by remember { mutableStateOf(false) }

    LaunchedEffect(checkForOverTheCounterResponse) {
        Log.d("CheckOverTheCounter", "Response: $checkForOverTheCounterResponse")
        if (checkForOverTheCounterResponse is NetworkResponse.Success) {
            val result = (checkForOverTheCounterResponse as NetworkResponse.Success).data
            isAllOverTheCounter = result.data == 0
        } else {
            isAllOverTheCounter = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                is NetworkResponse.Success -> {
                    val order = result.data.data.orderDetails
                    val products = result.data.data.productDetails

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

                    if (medicines.isNotEmpty()) {
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
                                        Log.d("CheckOverTheCounter", "Prescription already uploaded: ${order.prescription != null}")
                                        if (!isAllOverTheCounter && order.prescription == null) {
                                            Toast.makeText(context, "Some medicines are not over-the-counter and require a prescription", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        if (isEditing) {
                                            val productIDs = medicines.map { it.ProductID }
                                            val quantities = quantities.map { it.value }

                                            val orderBody = UpdateOrderBody(
                                                orderID = order.OrderID,
                                                productIDs = productIDs,
                                                quantities = quantities,
                                                removedProductIDs = removedMedicines,
                                                status = order.status
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
                                    "WAITING" -> Color.Yellow
                                    "WAITING FOR PICKUP" -> Color.Yellow
                                    "PROCESSING" -> Color.Blue
                                    "ACCEPT QUOTATION" -> Color.Yellow
                                    "DELIVERED" -> Color.Gray
                                    "USER PICKED UP" -> Color(0xFF4CAF50)
                                    "DELIVERY COMPLETED" -> Color.Magenta
                                    "REJECTED" -> Color.Red
                                    "DELIVERY FAILED" -> Color.Cyan
                                    "ACCEPTED" -> Color(0xFF4CAF50)
                                    "DELIVERY IN PROGRESS" -> Color.DarkGray
                                    else -> Color.Black
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (order.status == "ACCEPT QUOTATION" ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        // Handle Accept action
                                        accessToken?.let {
                                            viewModel.updateOrderStatus(
                                                it,
                                                orderId.toInt(),
                                                "A"
                                            ) { success ->
                                                if (success) {
                                                    Toast.makeText(
                                                        context,
                                                        "Order Accepted",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.getOrder(
                                                        it,
                                                        orderId.toInt()
                                                    ) // Reload the screen
                                                } else {
                                                    Log.d("OrderScreen", "Failed to accept order response: $success")
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to accept order",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text(text = "Accept")
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = {
                                        // Handle Reject action
                                        accessToken?.let {
                                            viewModel.updateOrderStatus(
                                                it,
                                                orderId.toInt(),
                                                "R"
                                            ) { success ->
                                                if (success) {
                                                    Toast.makeText(
                                                        context,
                                                        "Order Rejected",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.getOrder(
                                                        it,
                                                        orderId.toInt()
                                                    ) // Reload the screen
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to reject order",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text(text = "Reject")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (order.status == "ACCEPTED" && order.paymentMethod.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Select Payment Method",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                if (order.pickup == 1) {
                                    // Payment options for pickup
                                    Button(
                                        onClick = {
                                            accessToken?.let {
                                                viewModel.setPaymentMethod(it, order.OrderID, "cash") { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Payment set to Cash", Toast.LENGTH_SHORT).show()
                                                        viewModel.getOrder(it, orderId.toInt()) // Reload the screen
                                                    } else {
                                                        Toast.makeText(context, "Failed to set payment method", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Cash")
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Button(
                                        onClick = {
                                            accessToken?.let {
                                                viewModel.setPaymentMethod(it, order.OrderID, "card") { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Payment set to Card", Toast.LENGTH_SHORT).show()
                                                        viewModel.getOrder(it, orderId.toInt()) // Reload the screen
                                                    } else {
                                                        Toast.makeText(context, "Failed to set payment method", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Card")
                                    }
                                } else {
                                    // Payment options for delivery
                                    Button(
                                        onClick = {
                                            accessToken?.let {
                                                viewModel.setPaymentMethod(it, order.OrderID, "card") { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Payment set to Card", Toast.LENGTH_SHORT).show()
                                                        viewModel.getOrder(it, orderId.toInt()) // Reload the screen
                                                    } else {
                                                        Toast.makeText(context, "Failed to set payment method", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Card")
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Button(
                                        onClick = {
                                            accessToken?.let {
                                                viewModel.setPaymentMethod(it, order.OrderID, "cod") { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Payment set to Cash on Delivery", Toast.LENGTH_SHORT).show()
                                                        viewModel.getOrder(it, orderId.toInt()) // Reload the screen
                                                    } else {
                                                        Toast.makeText(context, "Failed to set payment method", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Cash on Delivery")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Log.d("PharmacyMedicines", "Pharmacy ID: ${order.PharmacyID}")
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

        // Chat button in the bottom right corner
        FloatingActionButton(
            onClick = {
                showChatDialog = true
                // Jump to the end of the chat list when opening
                coroutineScope.launch {
                    if (messages.value.isNotEmpty()) {
                        chatListState.animateScrollToItem(messages.value.size - 1)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .zIndex(10f)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Chat",
                tint = Color.White
            )
        }
    }

    // Chat dialog
    AnimatedVisibility(
        visible = showChatDialog,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300), initialOffsetY = { it }),
        exit = fadeOut(animationSpec = tween(300)) +
                slideOutVertically(animationSpec = tween(300), targetOffsetY = { it })
    ) {
        Dialog(onDismissRequest = { showChatDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.8f)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Messages",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { showChatDialog = false },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Chat",
                                tint = Color.White
                            )
                        }
                    }

                    // Messages list
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        if (messages.value.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No messages yet",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 16.dp),
                                state = chatListState,
                                reverseLayout = true
                            ) {
                                items(messages.value.size) { index ->
                                    val message = messages.value[index]
                                    val isUserMessage = message.sender == "user"
                                    val bubbleColor = if (isUserMessage) Color(0xFF4CAF50) else Color(0xFF2196F3)
                                    val textColor = Color.White
                                    val alignment = if (isUserMessage) Alignment.End else Alignment.Start

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 8.dp),
                                        contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
                                    ) {
                                        Column(
                                            horizontalAlignment = alignment
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(bubbleColor, RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                                    .widthIn(max = 280.dp)
                                            ) {
                                                Text(
                                                    text = message.comments,
                                                    color = textColor,
                                                    fontSize = 16.sp
                                                )
                                            }

                                            Text(
                                                text = message.createdAt,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    // Message input field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            placeholder = { Text("Type a message...") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .background(Color.White),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )

                        IconButton(
                            onClick = {
                                if (newMessage.isNotEmpty()) {
                                    accessToken?.let { token ->
                                        viewModel.sendMessage(token, newMessage, orderId.toInt()) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show()
                                                viewModel.getOrder(token, orderId.toInt()) // Reload to get new messages
                                                newMessage = ""
                                                // Scroll to bottom after sending message
                                                coroutineScope.launch {
                                                    if (messages.value.isNotEmpty()) {
                                                        chatListState.animateScrollToItem(messages.value.size - 1)
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Message",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMedicineDialog) {
        Log.d("PharmacyMedicines", "Pharmacy ID: $pharmacyID")

        if (pharmacyID != null) {
            MedicineSelectionDialog(
                onDismiss = { showMedicineDialog = false },
                onMedicineSelected = { medicine ->
                    medicines = medicines + medicine
                    quantities.add(mutableStateOf(1))
                    showMedicineDialog = false
                },
                selectedMedicines = medicines,
                searchType = 2,
                pharmacyId = pharmacyID!!
            )
        }
    }
}