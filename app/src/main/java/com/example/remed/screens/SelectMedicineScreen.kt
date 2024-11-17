package com.example.remed.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Checkbox
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remed.api.order.MedicineProduct


@Composable
fun SelectMedicinesScreen(navController: NavController) {
    val medicines = listOf(
        MedicineProduct(1, "Paracetamol"),
        MedicineProduct(2, "Ibuprofen"),
        MedicineProduct(3, "Aspirin"),
        MedicineProduct(4, "Amoxicillin")
    )

    val selectedMedicines = remember { mutableStateListOf<MedicineProduct>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Medicines", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(medicines) { medicine ->
                MedicineItem(
                    medicine = medicine,
                    isSelected = selectedMedicines.contains(medicine),
                    onItemClick = {
                        if (selectedMedicines.contains(medicine)) {
                            selectedMedicines.remove(medicine)
                        } else {
                            selectedMedicines.add(medicine)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Medicines:", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        for (medicine in selectedMedicines) {
            Text("- ${medicine.ProductName}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Navigate back to HomeScreen or perform an action with the selected medicines
            navController.navigateUp()
        }) {
            Text("Confirm Selection")
        }
    }
}



@Composable
fun MedicineItem(medicine: MedicineProduct, isSelected: Boolean, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isSelected, onCheckedChange = { onItemClick() })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = medicine.ProductName, fontSize = 18.sp)
    }
}
