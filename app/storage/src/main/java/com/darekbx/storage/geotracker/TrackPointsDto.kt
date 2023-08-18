package com.darekbx.storage.geotracker

import androidx.room.Embedded
import androidx.room.Entity
import com.darekbx.geotracker.repository.entities.TrackDto

@Entity
data class TrackPointsDto(
    @Embedded val trackDto: TrackDto,
    val pointsCount: Int
)