package com.darekbx.diggpl.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TokenRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val accessToken = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN] ?: ""
    }

    fun saveAccessToken(value: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN] = value
            }
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }
}
