package com.example.remed.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreAccessToken(private val context : Context) {
    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AccessToken")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    val getAccessToken: Flow<String?> = context.dataStore.data
        .map {  preferences ->
            preferences[ACCESS_TOKEN] ?: "None"
        }

    suspend fun saveAccessToken(token: String){
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }
}