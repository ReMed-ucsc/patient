package com.example.remed.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.remed.components.MedicineSelectionDialog
import com.example.remed.navigation.HomeRouteScreens
import com.example.remed.navigation.MainRouteScreens

@Composable
fun OrderScreen(navController: NavController, backStackEntry: NavBackStackEntry) {
    val selectedMedicineNames = backStackEntry.arguments?.getString("selectedMedicines") ?: ""
    var medicines by remember { mutableStateOf(selectedMedicineNames.split(",").filter { it.isNotEmpty() }) }
    var showMedicineDialog by remember { mutableStateOf(false) }
    var prescriptionUploaded by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf(TextFieldValue("")) }
    var addedPrescriptionUri by remember { mutableStateOf<Uri?>(null) }
    val prescriptionPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            addedPrescriptionUri = uri
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.White)
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
                    text = medicines[index],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { medicines = medicines - medicines[index] }) {
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

            Button(
                onClick = {
                    // Navigate to HistoryScreen
                    navController.navigate(MainRouteScreens.History.route)
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
                showMedicineDialog = false
            },
            selectedMedicines = medicines
        )
    }
}