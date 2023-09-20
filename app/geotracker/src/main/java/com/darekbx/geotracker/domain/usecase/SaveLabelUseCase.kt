package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import javax.inject.Inject

class SaveLabelUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(trackId: Long, label: String?) {
        repository.saveLabel(trackId, label)
    }
}
