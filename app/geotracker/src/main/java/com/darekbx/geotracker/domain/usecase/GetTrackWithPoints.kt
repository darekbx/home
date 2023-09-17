package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.repository.model.Track
import javax.inject.Inject

data class TrackWithPointsWrapper(val track: Track, val points: List<Point>)

class GetTrackWithPoints @Inject constructor(
    private val repository: BaseRepository
) {
    operator suspend fun invoke(trackId: Long): TrackWithPointsWrapper {
        val trackDto = repository.fetch(trackId)!!
        val points = repository.fetchTrackPoints(trackId).map {
            Point(
                it.timestamp,
                it.latitude,
                it.longitude,
                it.speed,
                it.altitude
            )
        }
        val track = Track(
            trackDto.id!!,
            trackDto.label,
            trackDto.startTimestamp,
            trackDto.endTimestamp,
            trackDto.distance,
            pointsCount = points.size
        )
        return TrackWithPointsWrapper(track, points)
    }
}