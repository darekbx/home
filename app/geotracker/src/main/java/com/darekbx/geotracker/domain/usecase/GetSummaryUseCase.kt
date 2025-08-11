package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.Summary
import com.darekbx.geotracker.repository.model.SummaryWrapper
import java.util.Calendar
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend fun getSummary(): SummaryWrapper {
        val allTracks = repository.fetchAllTracks().filter { it.endTimestamp != null }
        val yearTracks = repository.fetchYearTracks().filter { it.endTimestamp != null }

        val konaStartDate = konaStartDate()
        val onKonaUnit = repository.fetchAllTracks()
            .filter { it.startTimestamp > 0 }
            .filter { it.startTimestamp >= konaStartDate }
            .sumOf { it.distance?.toDouble() ?: 0.0 } / 1000

        val summary = Summary(
            allTracks.sumOf { it.distance?.toDouble() ?: 0.0 } / 1000,
            allTracks.sumOf { (it.endTimestamp ?: 0L) - it.startTimestamp } / 1000,
            allTracks.size
        )
        val yearSummary = Summary(
            yearTracks.sumOf { it.distance?.toDouble() ?: 0.0 } / 1000,
            yearTracks.sumOf { (it.endTimestamp ?: 0L) - it.startTimestamp } / 1000,
            yearTracks.size
        )

        return SummaryWrapper(summary, yearSummary, onKonaUnit)
    }

    private fun konaStartDate() = Calendar.getInstance().apply {
        set(2025, Calendar.JUNE, 4, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
