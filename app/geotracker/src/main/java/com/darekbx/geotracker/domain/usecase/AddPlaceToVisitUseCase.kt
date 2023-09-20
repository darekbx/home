package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PlaceDto
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class AddPlaceToVisitUseCase @Inject constructor(
    private val repository: BaseRepository
) {

    suspend operator fun invoke(label: String, geoPoint: GeoPoint) {
        repository.add(
            PlaceDto(
                null,
                label,
                geoPoint.latitude,
                geoPoint.longitude,
                currentTimeStamp()
            )
        )
    }

    private fun currentTimeStamp() = System.currentTimeMillis()
}