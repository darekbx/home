package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMaxSpeedUseCaseTest {

    @Test
    fun `Speed is -1, because table is empty`()  = runBlocking {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchMaxSpeed(): PointDto? = null
        }

        // When
        val speed = GetMaxSpeedUseCase(repository).getMaxSpeed()

        // Then
        assertEquals(-1F, speed, 0.1F)
    }

    @Test
    fun `Speed fetched successfully`()  = runBlocking {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchMaxSpeed() =
                PointDto(null, 1L, 1L, 0.0, 0.0, 52.2F, 0.0)
        }

        // When
        val speed = GetMaxSpeedUseCase(repository).getMaxSpeed()

        // Then
        assertEquals(187.92F, speed, 0.01F)
    }
}