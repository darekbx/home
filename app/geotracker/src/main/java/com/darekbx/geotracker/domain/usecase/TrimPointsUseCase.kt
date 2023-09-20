package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.model.Point
import javax.inject.Inject

class TrimPointsUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(trackId: Long, points: List<Point>) {
        repository.deleteAllPoints(trackId)
        repository.add(points.map {
            PointDto(
                null,
                trackId,
                it.timestamp,
                it.latitude,
                it.longitude,
                it.speed,
                it.altitude
            )
        })
    }
}