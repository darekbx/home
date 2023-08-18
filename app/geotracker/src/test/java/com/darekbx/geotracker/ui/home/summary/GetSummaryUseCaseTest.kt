package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSummaryUseCaseTest {

    @Test
    fun `Successfull getSummary when there is no data`() = runBlocking {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return emptyList()
            }
        }
        val useCase = GetSummaryUseCase(repository)

        // When
        val wrapper = useCase.getSummary()

        // Then
        with (wrapper.summary) {
            assertEquals(0, tripsCount)
            assertEquals(0.0, distance, 0.01)
            assertEquals(0, tripsCount)
        }
        with (wrapper.yearSummary) {
            assertEquals(0, tripsCount)
            assertEquals(0.0, distance, 0.01)
            assertEquals(0, tripsCount)
        }
    }

    @Test
    fun `Correct data for getSummary`() = runBlocking {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return listOf(
                    TrackDto(null, null, 10000, 19000, 5000.0F),
                    TrackDto(null, null, 10000, 20000, 2500.8F),
                    TrackDto(null, null, 10000, 250000, 15000.21F),
                    TrackDto(null, null, 20000, 450000, 25000.21F),
                    TrackDto(null, null, 30000, 550000, 105110.21F),
                )
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return listOf(
                    TrackDto(null, null, 10000, 19000, 5000.0F),
                    TrackDto(null, null, 10000, 20000, 2500.8F),
                    TrackDto(null, null, 10000, 250000, 15000.21F),
                )
            }
        }
        val useCase = GetSummaryUseCase(repository)

        // When
        val wrapper = useCase.getSummary()

        // Then
        with (wrapper.summary) {
            assertEquals(5, tripsCount)
            assertEquals(152.61, distance, 0.01)
            assertEquals(1209, time)
        }
        with (wrapper.yearSummary) {
            assertEquals(3, tripsCount)
            assertEquals(22.50, distance, 0.01)
            assertEquals(259, time)
        }
    }
}