package com.example.remed.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun OrderScreen(navController: NavController) {
    var medicines by remember { mutableStateOf(listOf<String>()) }
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
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical =  40.dp)
    ) {
        // List of selected medicines
        item{
            Text(
                text = "Selected Medicines",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(medicines.size) {index ->
            Text(
                text = medicines[index],
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )
        }

        item {

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add new medicine
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

            // Prescription upload section
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
                onClick =
                {
                    prescriptionUploaded = !prescriptionUploaded;
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

            // Comments section
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

    }

    if (showMedicineDialog) {
        MedicineSelectionDialog(
            onDismiss = { showMedicineDialog = false },
            onMedicineSelected = { medicine ->
                medicines = medicines + medicine
                showMedicineDialog = false
            }
        )
    }
}

@Composable
fun MedicineSelectionDialog(onDismiss: () -> Unit, onMedicineSelected: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val medicineList = listOf("Paracetamol", "Aspirin", "Ibuprofen", "Amoxicillin", "Metformin") // Example list

    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Search bar
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

            // List of medicines filtered by search query
            Column {
                medicineList.filter {
                    it.contains(searchQuery.text, ignoreCase = true)
                }.forEach { medicine ->
                    Text(
                        text = medicine,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onMedicineSelected(medicine) }
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun OrderScreenPreview() {
//    OrderScreen()
//}
