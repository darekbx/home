package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.SettingsRepository
import com.darekbx.geotracker.repository.entities.SimplePointDto
import javax.inject.Inject

class GetLatestTracksUseCase @Inject constructor(
    private val repository: BaseRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Map<Long, List<SimplePointDto>> {
        val pointsToSkip = settingsRepository.nthPointsToSkip()
        return repository.fetchYearTrackPoints(pointsToSkip)
    }
}