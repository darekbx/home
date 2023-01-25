package com.darekbx.weather.ui.weather

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.weather.data.WeatherRepository
import com.darekbx.weather.data.remote.airly.Measurements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val maxResults = dataStore.data.map { preferences ->
        if (preferences.contains(MAX_RESULTS)) preferences[MAX_RESULTS] ?: DEFAULT_MAX_RESULTS
        else DEFAULT_MAX_RESULTS
    }

    val maxDistance = dataStore.data.map { preferences ->
        if (preferences.contains(MAX_DISTANCE)) preferences[MAX_DISTANCE] ?: DEFAULT_MAX_DISTANCE
        else DEFAULT_MAX_DISTANCE
    }

    val antistormEnabled = dataStore.data.map { preferences ->
        preferences[ANTISTORM_ENABLED] ?: true
    }

    var measurementsList = mutableStateListOf<Measurements>()

    var stateHolder = mutableStateOf(0.0)

    val weatherConditions = flow {
        if (antistormEnabled.first()) {
            Log.v(TAG, "Load antistorm weather conditions")
            // Antistorm is not using location
            val data = weatherRepository.getImagesUrls(useAntistorm = true, 0.0, 0.0)
            emit(data)
        } else {
            Log.v(TAG, "Load rain viewer weather conditions")
            var currentLocation = locationProvider.currentLocation()
            if (currentLocation == null) {
                Log.v(TAG, "Current location is null, load last location")
                currentLocation = loadLastLocation()
            }
            if (currentLocation != null) {
                val lat = currentLocation.latitude
                val lng = currentLocation.longitude
                val data = weatherRepository.getImagesUrls(useAntistorm = false, lat, lng)
                emit(data)
            } else {
                Log.w(TAG, "Location is not available")
            }
        }
    }

    fun saveMaxDistance(value: Double) {
        runInIO {
            dataStore.edit { preferences ->
                preferences[MAX_DISTANCE] = value
            }
        }
    }

    fun saveMaxResults(value: Int) {
        runInIO {
            dataStore.edit { preferences ->
                preferences[MAX_RESULTS] = value
            }
        }
    }

    fun saveAntistormEnabled(value: Boolean) {
        runInIO {
            dataStore.edit { preferences ->
                preferences[ANTISTORM_ENABLED] = value
            }
        }
    }

    fun updateState() {
        stateHolder.value = Random.nextDouble()
    }

    fun loadAirQuality() {
        measurementsList.clear()
        runInIO {
            val maxDistance = maxDistance.first()
            val maxResults = maxResults.first()

            Log.v(TAG, "Load Air Quality")
            var currentLocation = locationProvider.currentLocation()

            if (currentLocation == null) {
                Log.v(TAG, "Current location is null, load last location")
                currentLocation = loadLastLocation()
            }

            if (currentLocation != null) {
                Log.w(TAG, "Load data for: $currentLocation")
                val installations = weatherRepository.readInstallations(
                    currentLocation.latitude, currentLocation.longitude,
                    maxDistance, maxResults
                )
                val installationsIds = installations.map { it.id }
                weatherRepository.readMeasurements(installationsIds) { measurement ->
                    if (measurement.temperature.isNotBlank()) {
                        measurement.installation = installations
                            .firstOrNull { it.id == measurement.installationId }
                        measurementsList.add(measurement)
                    }
                }

                persistCurrentLocation(currentLocation)
            } else {
                Log.w(TAG, "Location is not available")
            }
        }
    }

    private fun loadLastLocation(): Location? {
        var lastLocation: Location? = null
        dataStore.data.map { preferences ->
            if (preferences.contains(LAST_LOCATION_LAT) && preferences.contains(LAST_LOCATION_LNG)) {
                lastLocation = Location("").apply {
                    latitude = preferences[LAST_LOCATION_LAT]!!
                    longitude = preferences[LAST_LOCATION_LNG]!!
                }
                Log.v(TAG, "Last location was loaded")
            }
        }
        return lastLocation
    }

    private suspend fun persistCurrentLocation(currentLocation: Location) {
        dataStore.edit { preferences ->
            preferences[LAST_LOCATION_LAT] = currentLocation.latitude
            preferences[LAST_LOCATION_LNG] = currentLocation.longitude
        }
    }

    private fun runInIO(block: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"
        private val LAST_LOCATION_LAT = doublePreferencesKey("last_location_lat")
        private val LAST_LOCATION_LNG = doublePreferencesKey("last_location_lng")
        private val MAX_DISTANCE = doublePreferencesKey("max_distance")
        private val MAX_RESULTS = intPreferencesKey("max_results")
        private val ANTISTORM_ENABLED = booleanPreferencesKey("antistorm_enabled")

        const val DEFAULT_MAX_RESULTS = 5
        const val DEFAULT_MAX_DISTANCE = 5.0
    }
}
