package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FixEndTimestampUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    operator suspend fun invoke(trackId: Long) {
        repository.fetch(trackId)
            ?.takeIf { it.endTimestamp == null }
            ?.let { track ->
                val oneHour = TimeUnit.HOURS.toMillis(1)
                repository.update(trackId, track.startTimestamp + oneHour)
            }
    }
}
