package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.SettingsRepository
import com.darekbx.geotracker.repository.entities.SimplePointDto
import javax.inject.Inject

class GetAllTracksUseCase @Inject constructor(
    private val repository: BaseRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): List<List<SimplePointDto>> {
        val pointsToSkip = settingsRepository.nthPointsToSkip()
        return repository
            .fetchAllTrackPoints(pointsToSkip)
            .drop(1) // Skip actual track
    }
}