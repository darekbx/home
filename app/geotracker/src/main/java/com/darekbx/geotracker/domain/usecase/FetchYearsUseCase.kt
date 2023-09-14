package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class FetchYearsUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(): List<Int> {
        return repository.fetchYears()
    }
}
