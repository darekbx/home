package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveTrackUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    operator fun invoke(): Flow<Track?> {
        return repository.fetchLiveTrack()
            .map { track ->
                track?.let {
                    Track(
                        it.id!!,
                        it.label,
                        it.startTimestamp,
                        it.endTimestamp,
                        it.distance,
                        pointsCount = 0
                    )
                }
            }
    }
}
