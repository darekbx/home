package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.geotracker.repository.model.YearSummary
import java.util.Calendar
import javax.inject.Inject

class FetchStatisticsUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend operator fun invoke(): List<YearSummary> {
        val years = repository.fetchYears().reversed()
        return years.map { year ->
            val yearTracks = repository.fetchYearTracksStatistics(year)
            // max distance one day
            YearSummary(
                year = year,
                distance = yearTracks.sumDistance(),
                time = yearTracks.sumTime(),
                tripsCount = yearTracks.size,
                daysOnBike = yearTracks.countDays(),
                longestTrip = yearTracks.longestTrip(),
                maxDayDistance = yearTracks.maxDayDistance()
            )
        }
    }

    private fun List<TrackDto>.longestTrip() =
        maxOf { it.distance?.toDouble() ?: 0.0 } / 1000

    private fun List<TrackDto>.sumTime() =
        sumOf { (it.endTimestamp ?: 0L) - it.startTimestamp } / 1000

    private fun List<TrackDto>.sumDistance() =
        sumOf { it.distance?.toDouble() ?: 0.0 } / 1000

    private fun List<TrackDto>.maxDayDistance() =
        groupBy { byTrackDay(it) }
            .maxBy { dayTracks -> dayTracks.value.sumOf { it.distance?.toDouble() ?: 0.0 } }
            .value
            .sumDistance()

    private fun List<TrackDto>.countDays() = groupBy { byTrackDay(it) }.count()

    private fun byTrackDay(track: TrackDto): Int {
        val timeStamp = Calendar.getInstance().apply { timeInMillis = track.startTimestamp }
        return timeStamp.get(Calendar.DAY_OF_YEAR)
    }
}
