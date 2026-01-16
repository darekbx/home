package com.darekbx.emailbot.repository.storage

import androidx.datastore.preferences.core.*
import androidx.datastore.core.DataStore

class CommonPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val removedSpamCount = stringSetPreferencesKey("removedSpamCount_set")

    suspend fun incrementRemovedSpamCount(value: Int): Pair<Int, Int> {
        var sum = -1
        var keys = 0
        dataStore.edit { prefs ->
            val actual = prefs[removedSpamCount] ?: setOf("126") // actual value
            val updated = actual + "$value"
            prefs[removedSpamCount] = updated
            keys = updated.size
            sum = updated.map { it.toIntOrNull() }.filterNotNull().sum()
        }
        return sum to keys
    }
}
