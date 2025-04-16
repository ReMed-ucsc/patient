// OrderScreen.kt
package com.example.remed.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.remed.api.order.MedicineProduct
import com.example.remed.api.order.OrderBody
import com.example.remed.api.order.PharmacyData
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.components.PlacesAutocomplete
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import com.example.remed.navigation.MainRouteScreens
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@Composable
fun OrderScreen(navController: NavController, backStackEntry: NavBackStackEntry, viewModel: OrderViewModel) {
    val selectedMedicinesJson = backStackEntry.arguments?.getString("selectedMedicines") ?: ""
    val pharmacyJson = backStackEntry.arguments?.getString("pharmacy") ?: ""
    val gson = Gson()
    val typeMedicine = object : TypeToken<List<MedicineProduct>>() {}.type
    val typePharmacy = object : TypeToken<PharmacyData>() {}.type
    var medicines by remember { mutableStateOf(gson.fromJson<List<MedicineProduct>>(selectedMedicinesJson, typeMedicine)) }
    var pharmacy by remember { mutableStateOf(gson.fromJson<PharmacyData>(pharmacyJson, typePharmacy)) }
    var showMedicineDialog by remember { mutableStateOf(false) }
    var prescriptionUploaded by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf(TextFieldValue("")) }
    var addedPrescriptionUri by remember { mutableStateOf<Uri?>(null) }
    var isPickup by remember { mutableStateOf(true) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("") }

    val prescriptionPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            addedPrescriptionUri = uri
        }
    )

    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) { // Use a key to prevent recomposition triggers
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
    }

    // List to hold quantities
    var quantities by remember { mutableStateOf(medicines.map { mutableStateOf(1) }.toMutableList()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical =  40.dp)
    ) {
        item {
            Text(
                text = "Selected Medicines",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

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
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Quantity")
                }
                Text(text = quantities[index].value.toString(), fontSize = 16.sp, fontWeight = FontWeight.Normal)
                IconButton(onClick = { quantities[index].value++ }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Quantity")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
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
                onClick = { showMedicineDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Add Medicine")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selected Pharmacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Name: ${pharmacy.name}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "Address: ${pharmacy.address}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "Contact: ${pharmacy.contact}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "Available: ${pharmacy.availableCount} units",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Upload Prescription",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            AsyncImage(
                model = addedPrescriptionUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    prescriptionUploaded = !prescriptionUploaded
                    prescriptionPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (prescriptionUploaded) "Change Prescription" else "Upload Prescription")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Additional Comments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            BasicTextField(
                value = comments,
                onValueChange = { comments = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .padding(16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pickup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isPickup,
                    onCheckedChange = { isPickup = it }
                )
            }

            if (!isPickup) {
                Spacer(modifier = Modifier.height(8.dp))

                PlacesAutocomplete { place ->
                    selectedLocation = place.latLng
                    locationName = place.name ?: ""
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // check if a prescription is uploaded or at least one medicine is selected
                    if (addedPrescriptionUri == null && medicines.isEmpty()) {
                        Toast.makeText(context, "Please upload a prescription or select at least one medicine", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Collect data
                    val productIds = medicines.map { it.ProductID }
                    val quantitiesList = quantities.map { it.value }
                    val destinationText = if (isPickup) null else locationName
                    val destinationLatLng = if (isPickup) null else selectedLocation
                    val pharmacyId = pharmacy.PharmacyID

                    // Create OrderBody
                    val orderBody = OrderBody(
                        productIDs = productIds,
                        quantities = quantitiesList,
                        destination = destinationText,
                        pharmacyID = pharmacyId,
                        pickup = isPickup,
                        comments = comments.text,
                        prescriptionUri = addedPrescriptionUri?.toString(),
                        destinationLat = destinationLatLng?.latitude,
                        destinationLng = destinationLatLng?.longitude
                    )

                    Log.d("OrderScreen", "OrderBody: $orderBody")

                    // Call createOrder function
                    accessToken?.let {
                        viewModel.createOrder(context, it, orderBody, addedPrescriptionUri) { success ->
                            if (success) {
                                Toast.makeText(context, "Order is processing", Toast.LENGTH_SHORT).show()
                                navController.navigate(MainRouteScreens.History.route)
                            } else {
                                Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Place Order")
            }
        }
    }

    if (showMedicineDialog) {
        MedicineSelectionDialog(
            onDismiss = { showMedicineDialog = false },
            onMedicineSelected = { medicine ->
                medicines = medicines + medicine
                quantities.add(mutableStateOf(1)) // Add default quantity for new medicine
                showMedicineDialog = false
            },
            selectedMedicines = medicines
        )
    }
}