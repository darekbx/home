package com.darekbx.geotracker.domain.usecase

import android.location.Location
import com.darekbx.geotracker.firebase.FirebaseAuthenticationUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UploadLocationUseCase @Inject constructor(
    private val firebaseAuthenticationUtils: FirebaseAuthenticationUtils,
) {
    private var lastLocationTime: Long = 0L

    suspend fun upload(location: Location) {
        if (shouldThrottle(location.time)) return
        firebaseAuthenticationUtils.checkAndAuthorize()
        uploadLocation(location)
    }

    private fun shouldThrottle(locationTime: Long): Boolean {
        if (locationTime - lastLocationTime < MAX_UPLOAD_INTERVAL) {
            return true
        }
        lastLocationTime = locationTime
        return false
    }

    private suspend fun uploadLocation(location: Location) = suspendCoroutine { continuation ->
        Firebase
            .firestore
            .collection(CLOUD_LAST_LOCATION_COLLECTION)
            .document(CLOUD_LAST_LOCATION_RECORD)
            .set(
                hashMapOf(
                    "timestamp" to location.time,
                    "speed" to location.speed,
                    "altitude" to location.altitude,
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )
            )
            .addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener { continuation.resume(null) }
    }

    companion object {
        private const val CLOUD_LAST_LOCATION_COLLECTION = "last_location"
        private const val CLOUD_LAST_LOCATION_RECORD = "record"
        private const val MAX_UPLOAD_INTERVAL = 10_000 // ms
    }
}
