package com.darekbx.spreadsheet.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getLocalVersion(): Long =
        dataStore.data
            .map { preferences -> preferences[SYNC_LOCAL_VERSION] }
            .firstOrNull() ?: UNSET_LOCAL_VERSION

    suspend fun setLocalVersion(version: Long) {
        dataStore.edit { preferences ->
            preferences[SYNC_LOCAL_VERSION] = version
        }
    }

    suspend fun increaseLocalVersion() {
        dataStore.edit { preferences ->
            val currentVersion = preferences.get(SYNC_LOCAL_VERSION) ?: UNSET_LOCAL_VERSION
            preferences[SYNC_LOCAL_VERSION] = (currentVersion + 1)
        }
    }

    companion object {
        const val UNSET_LOCAL_VERSION = -1L
        private val SYNC_LOCAL_VERSION = longPreferencesKey("sync_local_version")
    }
}
