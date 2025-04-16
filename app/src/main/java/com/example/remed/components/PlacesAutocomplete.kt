package com.example.remed.components

import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.remed.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun PlacesAutocomplete(
    onPlaceSelected: (Place) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val density = LocalDensity.current

    // Initialize Places API
    if (!Places.isInitialized()) {
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
    }

    val placesClient = remember { Places.createClient(context) }
    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var isInputFocused by remember { mutableStateOf(false) }

    // Track the component's position and the keyboard height
    var componentBounds by remember { mutableStateOf(Rect.Zero) }
    var keyboardHeight by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }

    // Calculate whether predictions should appear above or below
    val shouldShowAbove = remember(componentBounds, keyboardHeight, screenHeight) {
        if (screenHeight == 0 || keyboardHeight == 0) {
            false
        } else {
            // Check if the bottom of the component would be covered by keyboard
            componentBounds.bottom > (screenHeight - keyboardHeight)
        }
    }

    // Get keyboard height and screen dimensions
    DisposableEffect(view) {
        val viewTreeObserver = view.viewTreeObserver
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            ViewCompat.getRootWindowInsets(view)?.let { insets ->
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                keyboardHeight = imeHeight
                screenHeight = view.height
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Show predictions above the search field when needed
        if (predictions.isNotEmpty() && isInputFocused && shouldShowAbove) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp
            ) {
                PredictionsList(
                    predictions = predictions,
                    placesClient = placesClient,
                    onPlaceSelected = { place ->
                        searchQuery = place.name ?: ""
                        predictions = emptyList()
                        isInputFocused = false
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onPlaceSelected(place)
                    }
                )
            }
        }

        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                if (query.length >= 3) {
                    isSearching = true
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
                .padding(bottom = 8.dp)
                .onFocusChanged { focusState ->
                    isInputFocused = focusState.isFocused
                }
                .onGloballyPositioned { coordinates ->
                    componentBounds = coordinates.boundsInRoot()
                },
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

        // Show predictions below the search field when not covered by keyboard
        if (predictions.isNotEmpty() && isInputFocused && !shouldShowAbove) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp
            ) {
                PredictionsList(
                    predictions = predictions,
                    placesClient = placesClient,
                    onPlaceSelected = { place ->
                        searchQuery = place.name ?: ""
                        predictions = emptyList()
                        isInputFocused = false
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onPlaceSelected(place)
                    }
                )
            }
        }
    }
}

@Composable
private fun PredictionsList(
    predictions: List<AutocompletePrediction>,
    placesClient: PlacesClient,
    onPlaceSelected: (Place) -> Unit
) {
    LazyColumn {
        items(predictions) { prediction ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
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
                                onPlaceSelected(response.place)
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