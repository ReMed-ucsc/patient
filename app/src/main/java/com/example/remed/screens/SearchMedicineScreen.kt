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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.components.PharmacyWithMedicineCard

@Composable
fun SearchMedicineScreen(navController: NavController) {
    var selectedMedicines by remember { mutableStateOf(listOf<String>()) }
    var pharmacies by remember { mutableStateOf(listOf<PharmacyWithMedicine>()) }
    var showSelectedMedicineList by remember { mutableStateOf(true) }
    var showPharmacyList by remember { mutableStateOf(false) }
    var showMedicineDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                            text = medicine,
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
                // Simulate fetching pharmacies with the selected medicine
                pharmacies = listOf(
                    PharmacyWithMedicine("Pharmacy A", "123 Main St, City", "123-456-7890", 10),
                    PharmacyWithMedicine("Pharmacy B", "456 Elm St, City", "987-654-3210", 5),
                    PharmacyWithMedicine("Pharmacy C", "789 Oak St, City", "555-555-5555", 8),
                )
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

data class PharmacyWithMedicine(
    val name: String,
    val address: String,
    val contact: String,
    val availableMedicines: Int
)