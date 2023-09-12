package com.darekbx.geotracker.ui.trips

import com.darekbx.geotracker.repository.BaseHomeRepository
import javax.inject.Inject

class FetchYearsUseCase @Inject constructor(
    private val homeRepository: BaseHomeRepository
) {

    suspend operator fun invoke(): List<Int> {
        return homeRepository.fetchYears()
    }
}
