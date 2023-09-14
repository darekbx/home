package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class DeleteTrackUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(trackId: Long) {
        repository.deleteTrack(trackId)
    }
}