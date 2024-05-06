package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveTrackPointsUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    fun getLastPoint() = repository.fetchLastPoint()

    operator fun invoke(): Flow<List<Point>> {
        return repository.fetchLivePoints()
            .map { list ->
                list.map {
                    Point(
                        it.timestamp,
                        it.latitude,
                        it.longitude,
                        it.speed,
                        it.altitude
                    )
                }
            }
    }
}
