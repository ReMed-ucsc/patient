// MedicineSelectionDialog.kt
package com.example.remed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.MedicineProduct
import com.example.remed.models.OrderViewModel

@Composable
fun MedicineSelectionDialog(
    onDismiss: () -> Unit,
    onMedicineSelected: (MedicineProduct) -> Unit,
    selectedMedicines: List<MedicineProduct>,
    viewModel: OrderViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val medicineListResponse by viewModel.medicineListResponse.observeAsState()

    // Call getMedicines when the search query changes
    LaunchedEffect(searchQuery.text) {
        viewModel.getMedicines(searchQuery.text)
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val result = medicineListResponse) {
                is NetworkResponse.Success -> {
                    val medicineList = result.data.data
                    val filteredList = medicineList.filter {
                        it.ProductName.contains(searchQuery.text, ignoreCase = true)
                    }

                    if (filteredList.isEmpty()) {
                        Text(
                            text = "No matching medicines found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Column {
                            filteredList.forEach { medicine ->
                                val isSelected = selectedMedicines.contains(medicine)
                                Text(
                                    text = medicine.ProductName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (isSelected) Color.Gray else Color.Black,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable(enabled = !isSelected) { onMedicineSelected(medicine) }
                                )
                            }
                        }
                    }
                }
                is NetworkResponse.Error -> {
                    Text(
                        text = "Failed to load medicines: ${result.message}",
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
}