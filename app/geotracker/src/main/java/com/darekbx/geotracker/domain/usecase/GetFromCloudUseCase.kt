package com.darekbx.geotracker.domain.usecase

import android.util.Log
import com.darekbx.geotracker.firebase.FirebaseAuthenticationUtils
import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Cloud structure
 *
 * track           - table contains tracks with points as json
 * track_ids       - table contains single array with sunchronized ids
 * last_location   - table contains last known location
 * places_to_visit - table contains places to visit
 */
class GetFromCloudUseCase @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val firebaseAuthenticationUtils: FirebaseAuthenticationUtils,
    private val repository: BaseRepository,
    private val gson: Gson
) {

    /**
     * Restore from cloud
     */
    @Throws(IllegalStateException::class, Exception::class)
    suspend fun restore(fromDate: Long) {
        firebaseAuthenticationUtils.checkAndAuthorize()

        Log.v(TAG, "User authorized, start synchronization")

        // 1. Fetch all local tracks and compare with remote tracks
        val localTracks = getTracksUseCase()

        // 2. Fetch remote tracks
        val remoteTracks = fetchTracksWithPointsByStartTimestamp(fromDate)

        Log.v(TAG, "${remoteTracks.size} to sync ...")

        // 3. Save
        remoteTracks.forEach { (track, points) ->
            val trackId = repository.add(track)
            points.forEach { point ->
                repository.add(point.copy(trackId = trackId))
            }
            Log.v(TAG, "Track $trackId saved")
        }

        Log.v(TAG, "Restore completed!")
    }

    private suspend fun fetchTracksWithPointsByStartTimestamp(
        startTimestamp: Long
    ): List<Pair<TrackDto, List<PointDto>>> {

        val snapshot = Firebase
            .firestore
            .collection(CLOUD_TRACK)
            .whereGreaterThan("start_timestamp", startTimestamp)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->

            val track = TrackDto(
                id = null,
                label = document.getString("label"),
                startTimestamp = document.getLong("start_timestamp") ?: return@mapNotNull null,
                endTimestamp = document.getLong("end_timestamp"),
                distance = document.getDouble("distance")?.toFloat()
            )

            val pointsJson = document.getString("points") ?: "[]"

            val type = object : TypeToken<List<CloudPoint>>() {}.type
            val cloudPoints: List<CloudPoint> = gson.fromJson(pointsJson, type)

            val points = cloudPoints.map {
                PointDto(
                    id = null,
                    trackId = 0L, // set later after DB insert if needed
                    timestamp = it.timestamp,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    speed = it.speed,
                    altitude = it.altitude
                )
            }

            track to points
        }
    }

    private data class CloudPoint(
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val speed: Float,
        val altitude: Double
    )

    companion object {
        private val TAG = GetFromCloudUseCase::class.simpleName
        private const val CLOUD_TRACK = "track"
        private const val CLOUD_TRACK_IDS = "track_ids"
    }
}
