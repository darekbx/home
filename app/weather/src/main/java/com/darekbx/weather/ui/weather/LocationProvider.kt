package com.darekbx.weather.ui.weather

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class LocationProvider(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): Location? = withTimeoutOrNull(2000L) {
        suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                }
            }
        }
    }
}