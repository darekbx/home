package com.darekbx.weather.ui.weather

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.weather.data.WeatherRepository
import com.darekbx.weather.data.network.airly.Measurements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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


    // Move to settings!
    val maxResults = 5
    val maxDistance = 10.0


    var measurementsList = mutableStateListOf<Measurements>()

    var stateHolder = mutableStateOf(0.0)

    val weatherConditions = flow {
        val data = weatherRepository.getImagesUrls()
        emit(data)
    }

    fun updateState() {
        stateHolder.value = Random.nextDouble()
    }

    fun loadAirQuality() {
        measurementsList.clear()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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

    private companion object {
        const val TAG = "WeatherViewModel"
        val LAST_LOCATION_LAT = doublePreferencesKey("last_location_lat")
        val LAST_LOCATION_LNG = doublePreferencesKey("last_location_lng")
    }
}
