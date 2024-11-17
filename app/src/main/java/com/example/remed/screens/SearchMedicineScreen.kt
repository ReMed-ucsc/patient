// SearchMedicineScreen.kt
package com.example.remed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.remed.api.order.MedicineProduct
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.components.PharmacyWithMedicineCard
import com.example.remed.models.OrderViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remed.api.NetworkResponse

@Composable
fun SearchMedicineScreen(navController: NavController, viewModel: OrderViewModel = viewModel()) {
    var selectedMedicines by remember { mutableStateOf(listOf<MedicineProduct>()) }
    val pharmacyWithMedicineListResponse by viewModel.pharmacyWithMedicineListResponse.observeAsState()
    var showSelectedMedicineList by remember { mutableStateOf(true) }
    var showPharmacyList by remember { mutableStateOf(false) }
    var showMedicineDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical =  40.dp)
    ) {
        // Medicine search bar
        Text(
            text = "Enter Medicine",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        if(showSelectedMedicineList) {
            LazyColumn {
                items(selectedMedicines) { medicine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = medicine.ProductName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedMedicines = selectedMedicines - medicine }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Medicine",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showMedicineDialog = true
                showSelectedMedicineList = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Add Medicine")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = {
                val productIds = selectedMedicines.map { it.ProductID }
                viewModel.searchPharmacies(productIDs = productIds)
                showPharmacyList = true
                showSelectedMedicineList = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Search Pharmacies")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pharmacies List using LazyColumn
        if (showPharmacyList) {
            when (val result = pharmacyWithMedicineListResponse) {
                is NetworkResponse.Success -> {
                    val pharmacies = result.data.data
                    Text(
                        text = "Pharmacies with Selected Medicine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        items(pharmacies) { pharmacy ->
                            PharmacyWithMedicineCard(pharmacy, navController, selectedMedicines)
                        }
                    }
                }
                is NetworkResponse.Error -> {
                    Text(
                        text = "Failed to load pharmacies: ${result.message}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                is NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                null -> {}
            }
        }
    }

    if (showMedicineDialog) {
        MedicineSelectionDialog(
            onDismiss = { showMedicineDialog = false },
            onMedicineSelected = { medicine ->
                selectedMedicines = selectedMedicines + medicine
                showMedicineDialog = false
            },
            selectedMedicines = selectedMedicines
        )
    }
}