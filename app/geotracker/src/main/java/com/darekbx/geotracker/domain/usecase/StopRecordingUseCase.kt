package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class StopRecordingUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    operator suspend fun invoke(trackId: Long, label: String?) {
        repository.updateTrack(trackId, currentTimestamp(), label)
    }

    fun currentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}
