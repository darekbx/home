package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.Track
import javax.inject.Inject

class TripsWrapper(val sumDistance: Double, val trips: List<Track>)

class FetchTripsUseCase @Inject constructor(
    private val repository: BaseRepository,
) {

    suspend operator fun invoke(year: Int): TripsWrapper {
        val tracks = repository.fetchYearTracks(year)
        val sumDistance = tracks.sumOf { it.trackDto.distance?.toDouble() ?: 0.0 }
        return TripsWrapper(
            sumDistance / 1000,
            tracks.map {
                Track(
                    it.trackDto.id!!,
                    it.trackDto.label,
                    it.trackDto.startTimestamp,
                    it.trackDto.endTimestamp,
                    (it.trackDto.distance ?: 0F) / 1000,
                    it.pointsCount
                )
            }
        )
    }
}
