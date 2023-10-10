package com.darekbx.infopigula.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingsRepository {

    suspend fun isDarkMode(): Boolean

    suspend fun clearCredentials()

    suspend fun accessToken(): String?

    suspend fun userUid(): String?

    suspend fun filteredGroups(): List<Int>

    suspend fun saveAuthCredentials(userUid: String, accessToken: String)

    suspend fun saveToken(accessToken: String)

    suspend fun saveDarkMode(isDarkMode: Boolean)

    suspend fun saveFilteredGroups(filteredGroupIds: List<Int>)
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

    override suspend fun filteredGroups() = dataStore.data
        .map { preferences -> preferences[FILTERED_GROUPS] }
        .firstOrNull()
        ?.map { it.toInt() }
        ?: emptyList()

    override suspend fun saveFilteredGroups(filteredGroupIds: List<Int>) {
        dataStore.edit { preferences ->
            preferences[FILTERED_GROUPS] = filteredGroupIds.map { "$it" }.toSet()
        }
    }

    override suspend fun saveAuthCredentials(userUid: String, accessToken: String) {
        dataStore.edit { preferences ->
            preferences[USER_UID] = userUid
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun saveToken(accessToken: String) {
        dataStore.edit { preferences ->
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
        val FILTERED_GROUPS = stringSetPreferencesKey("filtered_groups")

        private const val DEFAULT_IS_DARK_MODE = false
    }
}