package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class DeletePlaceToVisitUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(placeId: Long) {
        repository.deletePlaceToVisit(placeId)
    }
}