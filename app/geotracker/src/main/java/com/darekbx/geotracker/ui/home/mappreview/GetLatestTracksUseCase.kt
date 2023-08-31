package com.darekbx.geotracker.ui.home.mappreview

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.SettingsRepository
import com.darekbx.geotracker.repository.entities.SimplePointDto
import javax.inject.Inject

class GetLatestTracksUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Map<Long, List<SimplePointDto>> {
        val pointsToSkip = settingsRepository.nthPointsToSkip()
        return homeRepository.fetchYearTrackPoints(pointsToSkip)
    }
}