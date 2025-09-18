package com.darekbx.emailbot.repository.storage

import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import androidx.datastore.core.DataStore

class CommonPreferences(
    private val dataStore: DataStore<Preferences>
) {

    private val removedSpamCount = intPreferencesKey("removedSpamCount")

    suspend fun incrementRemovedSpamCount(value: Int) {
        dataStore.edit { prefs ->
            val currentCount = prefs[removedSpamCount] ?: 0
            prefs[removedSpamCount] = currentCount + value
        }
    }

    suspend fun loadRemovedSpamCount(): Int {
        val prefs = dataStore.data.first()
        return prefs[removedSpamCount] ?: 0
    }
}
