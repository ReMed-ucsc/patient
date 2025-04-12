package com.example.remed.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.remed.api.NetworkResponse
import com.example.remed.api.order.PharmacyData
import com.example.remed.components.PharmacyCard
import com.example.remed.components.PlacesAutocomplete
import com.example.remed.datastore.StoreAccessToken
import com.example.remed.models.OrderViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@Composable
fun SearchPharmacyScreen(navController: NavController, viewModel: OrderViewModel) {
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationAddress by remember { mutableStateOf("") }
    var range by remember { mutableStateOf(10f) } // Default range is 10 km

    val pharmacyListResponse = viewModel.pharmacyListResponse.observeAsState()

    val context = LocalContext.current
    val dataStore = StoreAccessToken(context)
    var accessToken by remember { mutableStateOf<String?>(null) }

    // Fetch the access token and call API only once
    LaunchedEffect(key1 = Unit) {
        accessToken = runBlocking { dataStore.getAccessToken.firstOrNull() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 40.dp)
    ) {
        // Location search bar
        Text(
            text = "Enter Location",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Places Autocomplete Component
        PlacesAutocomplete { place ->
            selectedPlace = place
            selectedLocation = place.latLng
            locationAddress = place.address ?: ""
            Log.d("SearchPharmacyScreen", "Selected place: $place")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Range Slider
        Text(
            text = "Select Range (km): ${range.toInt()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Slider(
            value = range,
            onValueChange = { range = it },
            valueRange = 1f..20f,
            steps = 19,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected location info if available
        selectedPlace?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = it.name ?: "Selected Location",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = it.address ?: "",
                        color = Color.Gray
                    )
                }
            }
        }

        // Set Location Button
        Button(
            onClick = {
                selectedLocation?.let { latLng ->
                    viewModel.searchNearbyPharmacies(lat = latLng.latitude, long = latLng.longitude, range = range.toInt())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = selectedLocation != null
        ) {
            Text(text = "Search Nearby Pharmacies")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display pharmacies based on API response
        when (val result = pharmacyListResponse.value) {
            is NetworkResponse.Success -> {
                val pharmacies: List<PharmacyData> = result.data.data
                Text(
                    text = "Nearby Pharmacies",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(pharmacies) { pharmacy ->
                        PharmacyCard(pharmacy, navController)
                    }
                }
            }
            is NetworkResponse.Error -> {
                Text(
                    text = result.message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            is NetworkResponse.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
            }
            null -> {
                // Initial state before any search
                Text(
                    text = "Select a location to find nearby pharmacies",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}