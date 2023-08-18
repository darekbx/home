package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.model.Summary
import com.darekbx.geotracker.repository.model.SummaryWrapper
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository
) {
    suspend fun getSummary(): SummaryWrapper {
        val allTracks = homeRepository.fetchAllTracks()
        val yearTracks = homeRepository.fetchYearTracks()

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

        return SummaryWrapper(summary, yearSummary)
    }
}
