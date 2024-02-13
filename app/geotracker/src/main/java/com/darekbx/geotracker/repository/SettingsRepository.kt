package com.darekbx.geotracker.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun isDarkMode() = true

    suspend fun nthPointsToSkip(): Int =
        dataStore.data.map { preferences ->
            preferences[NTH_POINTS_TO_SKIP]
        }.firstOrNull() ?: DEFAULT_NTH_POINTS_TO_SKIP

    suspend fun gpsMinDistance(): Float =
        dataStore.data.map { preferences ->
            preferences[GPS_MIN_DISTANCE]
        }.firstOrNull() ?: DEFAULT_GPS_MIN_DISTANCE

    suspend fun gpsUpdateInterval(): Long =
        dataStore.data.map { preferences ->
            preferences[GPS_UPDATE_INTERVAL]
        }.firstOrNull() ?: DEFAULT_GPS_UPDATE_INTERVAL

    suspend fun showYearSummary(): Boolean =
        dataStore.data.map { preferences ->
            preferences[SHOW_YEAR_SUMMARY]
        }.firstOrNull() ?: DEFAULT_SHOW_YEAR_SUMMARY

    suspend fun saveSettings(
        nthPointsToSkip: Int,
        gpsMinDistance: Float,
        gpsUpdateInterval: Long,
        showYearSummary: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[NTH_POINTS_TO_SKIP] = nthPointsToSkip
            preferences[GPS_MIN_DISTANCE] = gpsMinDistance
            preferences[GPS_UPDATE_INTERVAL] = gpsUpdateInterval
            preferences[SHOW_YEAR_SUMMARY] = showYearSummary
        }
    }

    companion object {
        private const val DEFAULT_NTH_POINTS_TO_SKIP = 2
        private const val DEFAULT_GPS_MIN_DISTANCE = 20F
        private const val DEFAULT_GPS_UPDATE_INTERVAL = 20L
        private const val DEFAULT_SHOW_YEAR_SUMMARY = true

        private val NTH_POINTS_TO_SKIP = intPreferencesKey("nth_points_to_skip")
        private val GPS_MIN_DISTANCE = floatPreferencesKey("gps_min_distance")
        private val GPS_UPDATE_INTERVAL = longPreferencesKey("gps_update_interval")
        private val SHOW_YEAR_SUMMARY = booleanPreferencesKey("show_year_summary")
    }
}