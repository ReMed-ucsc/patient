package com.example.remed.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.remed.database.Reminder
import com.example.remed.models.ReminderViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderScreen(navController: NavController, viewModel: ReminderViewModel = hiltViewModel()) {
    var reminders by remember { mutableStateOf(listOf<Reminder>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf<Reminder?>(null) }


    LaunchedEffect(Unit) {
        viewModel.getAllReminders().observeForever { dbReminders ->
            reminders = dbReminders
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedReminder = null // Reset for new reminder
                    showDialog = true
                },
                modifier = Modifier.padding(bottom = 72.dp)
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
                            ReminderCard(
                                reminder = reminder,
                                onEdit = { selectedReminder = it; showDialog = true },
                                onDelete = { viewModel.deleteReminder(it) }
                            )
                        }
                    }
                }
            }
        }
    )

    if (showDialog) {
        AddReminderDialog(
            onDismiss = { showDialog = false },
            onSave = { reminder ->
                if (selectedReminder != null) {
                    viewModel.updateReminder(reminder.copy(id = selectedReminder!!.id))
                } else {
                    viewModel.insertReminder(reminder)
                }
                showDialog = false
            },
            initialReminder = selectedReminder
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit,
    initialReminder: Reminder? = null
) {
    var drugName by remember { mutableStateOf(initialReminder?.drugName ?: "") }
    var dosage by remember { mutableStateOf(initialReminder?.dosage ?: "") }
    var additionalInfo by remember { mutableStateOf(initialReminder?.additionalInfo ?: "") }
    var selectedTime by remember { mutableStateOf(initialReminder?.time ?: "") }


    // Time picker state
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialReminder?.time?.split(":")?.get(0)?.toInt() ?: 0,
        initialMinute = initialReminder?.time?.split(":")?.get(1)?.split(" ")?.get(0)?.toInt() ?: 0
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialReminder != null) "Edit Reminder" else "Add Reminder") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = drugName,
                    onValueChange = { drugName = it },
                    label = { Text("Drug Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Time selector button
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedTime.isNotEmpty()) "Time: $selectedTime" else "Select Time")
                }

                OutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    label = { Text("Additional Info") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reminder = Reminder(
                        drugName = drugName,
                        dosage = dosage,
                        time = selectedTime,
                        additionalInfo = additionalInfo
                    )
                    onSave(reminder)
                },
                enabled = drugName.isNotEmpty() && dosage.isNotEmpty() && selectedTime.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Time Picker Dialog
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TimePicker(state = timePickerState)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                // Format time as string (e.g., "8:30 AM")
                                val hour = timePickerState.hour
                                val minute = timePickerState.minute
                                val localTime = LocalTime.of(hour, minute)
                                selectedTime = localTime.format(
                                    DateTimeFormatter.ofPattern("h:mm a")
                                )
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onEdit: (Reminder) -> Unit, onDelete: (Reminder) -> Unit) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onEdit(reminder) }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onDelete(reminder) }) {
                    Text("Delete")
                }
            }
        }
    }
}