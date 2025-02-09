package com.darekbx.geotracker.domain.usecase

import android.util.Log
import com.darekbx.geotracker.firebase.FirebaseAuthenticationUtils
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.geotracker.repository.model.PlaceToVisit
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Cloud structure
 *
 * track           - table contains tracks with points as json
 * track_ids       - table contains single array with sunchronized ids
 * last_location   - table contains last known location
 * places_to_visit - table contains places to visit
 */
class SynchronizeUseCase @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val getTrackPointsUseCase: GetTrackPointsUseCase,
    private val getPlacesToVisitUseCase: GetPlacesToVisitUseCase,
    private val firebaseAuthenticationUtils: FirebaseAuthenticationUtils,
    private val gson: Gson
) {
    var onProgress: (Int, Int) -> Unit = { _, _ -> }

    /**
     * Count data to synchronize
     */
    fun dataToSynchronize() = flow {
        firebaseAuthenticationUtils.checkAndAuthorize()

        val localTracks = getTracksUseCase()
        val remoteTrackIds = fetchRemoteIds()
        val tracksToSynchronize = filterObjectsById(remoteTrackIds, localTracks)
        emit(tracksToSynchronize.size)
    }

    /**
     * Synchronize tracks
     */
    @Throws(IllegalStateException::class, Exception::class)
    suspend fun synchronize() {
        firebaseAuthenticationUtils.checkAndAuthorize()

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

        Log.v(TAG, "Synchronize places to visit")
        synchronizePlacesToVisit()

        Log.v(TAG, "Synchronization completed!")
    }

    /**
     * Synchronize places to visit
     */
    private suspend fun synchronizePlacesToVisit() {
        // 1. Fetch local data
        val placesToVisitLocal = getPlacesToVisitUseCase()

        // 2. Remote remote data
        deleteDocuments(PLACES_TO_VISIT)

        // 3. Upload local data
        Log.v(TAG, "Upload ${placesToVisitLocal.size} documents")
        placesToVisitLocal.forEach { item ->
            addPlaceToVisit(item)
        }
    }

    private suspend fun addPlaceToVisit(item: PlaceToVisit) {
        Firebase
            .firestore
            .collection(PLACES_TO_VISIT)
            .add(
                hashMapOf(
                    "label" to item.label,
                    "latitude" to item.latitude,
                    "longitude" to item.longitude
                )
            )
            .asDeferred()
            .await()
    }

    private suspend fun deleteDocuments(collection: String) {
        try {
            val documents = Firebase
                .firestore
                .collection(collection)
                .get()
                .asDeferred()
                .await()
            var count = 0
            documents.forEach { document ->
                document.reference
                    .delete()
                    .asDeferred()
                    .await()
                count++
            }
            Log.v(TAG, "Deleted $count documents")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete documents!", e)
        }
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
        private const val PLACES_TO_VISIT = "places_to_visit"
    }
}