package com.darekbx.geotracker.domain.usecase

import android.location.Location
import android.util.Log
import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddLocationUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    private var lastLocation: Location? = null
    private var sessionDistance = 0.0F

    suspend operator fun invoke(location: Location): Float {
        // Fetch tracks without endtime
        val unfinishedTracks = repository.fetchUnFinishedTracks()
        // Get first unfinished track or create new one
        val latestTrackId = unfinishedTracks.firstOrNull()?.id
            ?: addNewTrack()

        if (unfinishedTracks.size > 1) {
            Log.e(TAG, "More than one unfinshed track!")
        }

        repository.add(
            PointDto(
                id = null,
                trackId = latestTrackId,
                timestamp = System.currentTimeMillis(),
                latitude = location.latitude,
                longitude = location.longitude,
                speed = location.speed,
                altitude = location.altitude
            )
        )

        appendDistance(location, latestTrackId)
        lastLocation = location
        return sessionDistance
    }

    suspend fun addManuallyTrack(distance: Float, startTime: Long, endTime: Long): Long {
        return repository.add(
            TrackDto(
                id = null,
                label = null,
                startTimestamp = startTime,
                endTimestamp = endTime,
                distance = distance
            )
        )
    }

    suspend fun addNewTrack(): Long {
        return repository.add(
            TrackDto(
                id = null,
                label = null,
                startTimestamp = System.currentTimeMillis(),
                distance = 0F
            )
        )
    }

    suspend fun appendDistance(location: Location, trackId: Long) {
        withContext(Dispatchers.IO) {
            lastLocation?.distanceTo(location)?.let { distance ->
                repository.appendDistance(trackId, distance)
                sessionDistance += distance
            }
            lastLocation = location
        }
    }

    companion object {
        private val TAG = "AddLocationUseCase"
    }
}