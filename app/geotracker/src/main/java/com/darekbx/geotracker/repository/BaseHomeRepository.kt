package com.darekbx.geotracker.repository

import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto

interface BaseHomeRepository {

    suspend fun fetchAllTracks(): List<TrackDto>

    suspend fun fetchYearTracks(): List<TrackDto>

    suspend fun fetchMaxSpeed(): PointDto?
}
