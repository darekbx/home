package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.model.PlaceToVisit
import javax.inject.Inject

class GetPlacesToVisitUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend operator fun invoke(): List<PlaceToVisit> {
        return repository.fetchPlacesToVisit().map {
            PlaceToVisit(it.id!!, it.label, it.latitude, it.longitude, it.timestamp)
        }
    }
}
