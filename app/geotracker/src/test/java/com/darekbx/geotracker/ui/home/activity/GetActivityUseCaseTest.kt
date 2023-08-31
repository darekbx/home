package com.darekbx.geotracker.ui.home.activity

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActivityUseCaseTest {

    @Test
    fun `Activity data successfully fetched`() = runBlocking {
        // Given
        val repository = object : BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return listOf(
                    TrackDto(null, null, 1672579053, 1, 5000.0F),
                    TrackDto(null, null, 1672599053, 1, 5000.0F),
                    TrackDto(null, null, 1672665453, 1, 2500.8F),
                    TrackDto(null, null, 1672751853, 1, 15000.21F)
                );
            }

            override suspend fun fetchYearTrackPoints(nthPointsToSkip: Int): Map<Long, List<SimplePointDto>> {
                return emptyMap()
            }

            override suspend fun fetchMaxSpeed(): PointDto? = null
        }

        // When
        val activity = GetActivityUseCase(repository).getActivityData()

        // Then
        assertEquals(3, activity.size)
        assertEquals(10000.0, activity[0].distance, 0.01)
        assertEquals(2500.80, activity[1].distance, 0.01)
        assertEquals(15000.20, activity[2].distance, 0.01)
    }
}