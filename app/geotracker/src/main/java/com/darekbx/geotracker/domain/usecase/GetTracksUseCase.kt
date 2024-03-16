package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend operator fun invoke(): List<TrackDto> {
        return repository.fetchAllTracks()
    }
}
