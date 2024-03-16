package com.darekbx.geotracker.domain.usecase

import android.util.Log
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Cloud structure
 *
 * track        - table contains tracks with points as json
 * track_ids    - table contains single array with sunchronized ids
 */
class SynchronizeUseCase @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val getTrackPointsUseCase: GetTrackPointsUseCase,
    private val gson: Gson
) {
    var onProgress: (Int, Int) -> Unit = { _, _ -> }

    /**
     * Count data to synchronize
     */
    fun dataToSynchronize(email: String, password: String) = flow {
        val localTracks = getTracksUseCase()
        val remoteTrackIds = fetchRemoteIds()
        val tracksToSynchronize = filterObjectsById(remoteTrackIds, localTracks)
        emit(tracksToSynchronize.size)
    }

    @Throws(IllegalStateException::class, Exception::class)
    suspend fun synchronize(email: String, password: String) {
        if (!isAuthorized()) {
            authorize(email, password)
                ?: throw IllegalStateException("Unabled to login!")
        }

        Log.v(TAG, "User authorized, start synchronization")

        // 1. Fetch all local tracks and compare with remote tracks
        val localTracks = getTracksUseCase()
        val remoteTrackIds = fetchRemoteIds()
        Log.v(TAG, "Local tracks count: ${localTracks.size}")
        Log.v(TAG, "Remote tracks count: ${remoteTrackIds.size}")

        // 2. Calculate diff
        val tracksToSynchronize = filterObjectsById(remoteTrackIds, localTracks)
        Log.v(TAG, "Tracks to synchronize: ${tracksToSynchronize.size}")

        if (tracksToSynchronize.isEmpty()) {
            Log.v(TAG, "Nothing to synchronize!")
            onProgress(0, 0)
            return
        }

        // 3. Upload missing local tracks to the cloud
        var counter = 0
        tracksToSynchronize.forEach { track ->
            val trackId = track.id!!
            val trackPoints = getTrackPointsUseCase.invoke(trackId)
            addLocalTrack(track, trackPoints)
            saveRemoteIds(trackId)
            onProgress(++counter, tracksToSynchronize.size)
            Log.v(TAG, "Saved local track to cloud, id: $trackId")

            delay(1000L) // Throttle to avoid "Write stream exhausted maximum allowed queued writes"
        }

        Log.v(TAG, "Synchronization completed!")
    }

    private fun filterObjectsById(remoteTrackIds: List<Long>, localTracks: List<TrackDto>): List<TrackDto> {
        return localTracks.filter { !remoteTrackIds.contains(it.id) }
    }

    private suspend fun fetchRemoteIds() = suspendCoroutine<List<Long>> { continuation ->
        Firebase
            .firestore
            .collection(CLOUD_TRACK_IDS)
            .get()
            .addOnSuccessListener { documents ->
                    val ids = mutableListOf<Long>()
                    for(document in documents) {
                        ids.add(document.getLong("id")!!)
                    }
                    continuation.resume(ids)
            }
            .addOnFailureListener { e -> continuation.resumeWithException(e) }
    }

    private suspend fun saveRemoteIds(id: Long) = suspendCoroutine { continuation ->
        Firebase
            .firestore
            .collection(CLOUD_TRACK_IDS)
            .add(hashMapOf("id" to id))
            .addOnSuccessListener { continuation.resume(Unit) }
            .addOnFailureListener { e -> continuation.resumeWithException(e) }
    }

    private suspend fun addLocalTrack(
        track: TrackDto,
        points: List<PointDto>
    ) = suspendCoroutine { continuation ->
        val cloudPoints = points.map {
            CloudPoint(it.timestamp, it.latitude, it.longitude, it.speed, it.altitude)
        }
        Firebase
            .firestore
            .collection(CLOUD_TRACK)
            .add(
                hashMapOf(
                    "local_id" to track.id,
                    "label" to track.label,
                    "start_timestamp" to track.startTimestamp,
                    "end_timestamp" to track.endTimestamp,
                    "distance" to track.distance,
                    "points" to gson.toJson(cloudPoints)
                )
            )
            .addOnSuccessListener { continuation.resume(it.id) }
            .addOnFailureListener { e -> continuation.resumeWithException(e) }
    }

    private fun isAuthorized() = Firebase.auth.currentUser != null

    private suspend fun authorize(email: String, password: String) =
        suspendCoroutine { continuation ->
            Firebase.auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(it.user) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

    private data class CloudPoint(
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val speed: Float,
        val altitude: Double
    )

    companion object {
        private val TAG = SynchronizeUseCase::class.simpleName
        private const val CLOUD_TRACK = "track"
        private const val CLOUD_TRACK_IDS = "track_ids"
    }
}