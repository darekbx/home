package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import javax.inject.Inject

class GetRecordingStateUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    operator suspend fun invoke(): TrackDto? {
        val unfinishedTracks = repository.fetchUnFinishedTracks()
        return unfinishedTracks.firstOrNull()
    }
}