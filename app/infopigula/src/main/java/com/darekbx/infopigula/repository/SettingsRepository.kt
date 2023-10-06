package com.darekbx.infopigula.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingsRepository {

    suspend fun isDarkMode(): Boolean

    suspend fun clearCredentials()

    suspend fun accessToken(): String?

    suspend fun userUid(): String?

    suspend fun saveAuthCredentials(userUid: String, accessToken: String)

    suspend fun saveDarkMode(isDarkMode: Boolean)
}

class DefaultSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override suspend fun isDarkMode() =
        dataStore.data
            .map { preferences -> preferences[IS_DARK_MODE] }
            .firstOrNull()
            ?: DEFAULT_IS_DARK_MODE

    override suspend fun accessToken() = dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN] }
        .firstOrNull()

    override suspend fun userUid() = dataStore.data
        .map { preferences -> preferences[USER_UID] }
        .firstOrNull()

    override suspend fun saveAuthCredentials(userUid: String, accessToken: String) {
        dataStore.edit { preferences ->
            preferences[USER_UID] = userUid
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun clearCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(USER_UID)
        }
    }

    override suspend fun saveDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }

    companion object {
        val USER_UID = stringPreferencesKey("user_uid")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        private const val DEFAULT_IS_DARK_MODE = false
    }
}