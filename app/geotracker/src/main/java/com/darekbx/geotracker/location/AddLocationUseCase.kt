package com.darekbx.geotracker.location

import android.location.Location
import android.util.Log
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.TrackDao
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import javax.inject.Inject

class AddLocationUseCase @Inject constructor(
    private val pointDao: PointDao,
    private val trackDao: TrackDao
) {
    suspend operator fun invoke(location: Location) {
        // Fetch tracks without endtime
        val unfinishedTracks = trackDao.fetchUnFinishedTracks()
        // Get first unfinished track or create new one
        val latestTrackId = unfinishedTracks.firstOrNull()?.id
            ?: addNewTrack()

        if (unfinishedTracks.size > 1) {
            Log.e(TAG, "More than one unfinshed track!")
        }

        pointDao.add(
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
    }

    suspend fun addNewTrack(): Long {
        return trackDao.add(
            TrackDto(
                id = null,
                label = null,
                startTimestamp = System.currentTimeMillis()
            )
        )
    }

    companion object {
        private val TAG = "AddLocationUseCase"
    }
}