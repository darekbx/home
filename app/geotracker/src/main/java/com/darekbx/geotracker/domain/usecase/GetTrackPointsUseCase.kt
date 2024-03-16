package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import javax.inject.Inject

class GetTrackPointsUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    operator suspend fun invoke(trackId: Long): List<PointDto> {
        return repository.fetchTrackPoints(trackId)
    }
}
