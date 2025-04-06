package com.example.remed.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.remed.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
@Composable
fun PlacesAutocomplete(
    onPlaceSelected: (Place) -> Unit
) {
    val context = LocalContext.current

    // Initialize Places API
    if (!Places.isInitialized()) {
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
    }

    val placesClient = remember { Places.createClient(context) }
    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                if (query.length >= 3) {
                    isSearching = true
                    // Create a new prediction request
                    val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build()

                    placesClient.findAutocompletePredictions(predictionsRequest)
                        .addOnSuccessListener { response ->
                            predictions = response.autocompletePredictions
                            isSearching = false
                        }
                        .addOnFailureListener { exception ->
                            Log.e("PlacesAutocomplete", "Prediction fetching failed: ${exception.message}")
                            isSearching = false
                        }
                } else {
                    predictions = emptyList()
                    isSearching = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Search for a location") },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            singleLine = true
        )

        // Predictions dropdown
        if (predictions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp
            ) {
                LazyColumn {
                    items(predictions) { prediction ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Fetch place details when place is selected
                                    val placeFields = listOf(
                                        Place.Field.ID,
                                        Place.Field.NAME,
                                        Place.Field.ADDRESS,
                                        Place.Field.LAT_LNG
                                    )

                                    val fetchPlaceRequest = FetchPlaceRequest.builder(
                                        prediction.placeId, placeFields
                                    ).build()

                                    placesClient.fetchPlace(fetchPlaceRequest)
                                        .addOnSuccessListener { response ->
                                            val place = response.place
                                            searchQuery = place.name ?: ""
                                            predictions = emptyList() // Clear predictions
                                            onPlaceSelected(place)
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("PlacesAutocomplete", "Place fetch failed: ${exception.message}")
                                        }
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = prediction.getPrimaryText(null).toString(),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = prediction.getSecondaryText(null).toString(),
                                color = Color.Gray
                            )
                            Divider(modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }
            }
        }
    }
}