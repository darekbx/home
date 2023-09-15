package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.geotracker.repository.model.ActivityData
import java.util.Calendar
import javax.inject.Inject

class GetActivityUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend fun getActivityData(): List<ActivityData> {
        val yearTracks = repository.fetchYearTracks()
        return yearTracks
            .groupBy { track -> getDayOfYearFromTimestamp(track, track.startTimestamp) }
            .map { group ->
                val distances = group.value.map { it.distance?.toDouble() ?: 0.0 }
                ActivityData(group.key, distances)
            }
    }

    private fun getDayOfYearFromTimestamp(trackDto: TrackDto, timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.DAY_OF_YEAR)
    }
}