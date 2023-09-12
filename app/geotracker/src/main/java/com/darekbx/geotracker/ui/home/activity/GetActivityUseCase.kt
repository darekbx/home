package com.darekbx.geotracker.ui.home.activity

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.model.ActivityData
import java.util.Calendar
import javax.inject.Inject

class GetActivityUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository
) {
    suspend fun getActivityData(): List<ActivityData> {
        val yearTracks = homeRepository.fetchYearTracks()
        return yearTracks
            .groupBy { track -> getDayOfYearFromTimestamp(track.startTimestamp) }
            .map { group ->
                val distances = group.value.map { it.distance?.toDouble() ?: 0.0 }
                ActivityData(group.key, distances)
            }
    }

    private fun getDayOfYearFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        return calendar.get(Calendar.DAY_OF_YEAR)
    }
}