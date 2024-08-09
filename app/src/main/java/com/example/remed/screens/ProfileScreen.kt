package com.example.remed.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remed.R

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("Hansaja Kithmal") }
    var email by remember { mutableStateOf("hkd@gmail.com") }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Image(
            painter = painterResource(id = R.drawable.heart),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp),
            contentScale = ContentScale.Crop
        )

        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Button(
                onClick = { isEditing = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Details")
            }
        } else {
            Text(text = "Name: $name", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Email: $email", fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Edit Details")
            }
        }
    }
}
