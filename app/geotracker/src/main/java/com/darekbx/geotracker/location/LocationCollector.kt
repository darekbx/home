package com.darekbx.geotracker.location

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import com.darekbx.geotracker.repository.SettingsRepository
import com.darekbx.geotracker.system.BaseLocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationCollector @Inject constructor(
    private val locationManager: BaseLocationManager,
    private val settingsRepository: SettingsRepository
) {

    fun locationFlow(): Flow<Location> {
        return _locationUpdates
    }

    @SuppressLint("MissingPermission")
    private val _locationUpdates = callbackFlow {
        val minDistance = settingsRepository.gpsMinDistance()
        val updateInterval = settingsRepository.gpsUpdateInterval()

        val locationListener = LocationListener { location ->
            //Log.v(TAG, "Location changed: $location")
            trySend(location)
        }

        Log.v(TAG, "Request location updates")
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            updateInterval,
            minDistance,
            locationListener
        )

        awaitClose {
            Log.v(TAG, "Stop location updates")
            locationManager.removeUpdates(locationListener)
        }
    }.flowOn(Dispatchers.Main)

    companion object {
        private const val TAG = "LocationCollector"
    }
}
