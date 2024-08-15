package com.example.remed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@Composable
fun ReminderScreen(navController: NavController) {
    var reminders by remember { mutableStateOf(listOf<Reminder>()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                // Add logic to add new reminder
                val newReminder = Reminder(
                    drugName = "Drug A",
                    dosage = "1 pill",
                    time = "8:00 AM",
                    additionalInfo = "Take with food"
                )
                reminders = reminders + newReminder
            }, modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reminder")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Medication Reminders",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )

                if (reminders.isEmpty()) {
                    Text(
                        text = "No reminders added yet.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(reminders) { reminder ->
                            ReminderCard(reminder)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ReminderCard(reminder: Reminder) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = reminder.drugName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dosage: ${reminder.dosage}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Time: ${reminder.time}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Additional Info: ${reminder.additionalInfo}",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

data class Reminder(
    val drugName: String,
    val dosage: String,
    val time: String,
    val additionalInfo: String
)

// @Preview(showBackground = true)
// @Composable
// fun ReminderScreenPreview() {
//     ReminderScreen(navController = /* Provide NavController here */)
// }
