package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class GetCountPlacesUseCase @Inject constructor(
    private val repository: BaseRepository
) {
    suspend operator fun invoke(): Int {
        return repository.countPlacesToVisit()
    }
}
