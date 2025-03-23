package com.example.remed.components

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@Composable
fun PlacesAutocomplete(onPlaceSelected: (Place) -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            onPlaceSelected(place)
        }
    }

    val intent = remember {
        Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            .build(context)
    }

    Button(onClick = { autocompleteLauncher.launch(intent) }) {
        Text(text = "Select Location")
    }
}