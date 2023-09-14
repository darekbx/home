package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSummaryUseCaseTest {

    @Test
    fun `Successfull getSummary when there is no data`() = runTest {
        // Given
        val repository = mockk<BaseRepository>()
        coEvery { repository.fetchYearTracks() } returns emptyList()
        coEvery { repository.fetchAllTracks() } returns emptyList()
        val useCase = GetSummaryUseCase(repository)

        // When
        val wrapper = useCase.getSummary()

        // Then
        with(wrapper.summary) {
            assertEquals(0, tripsCount)
            assertEquals(0.0, distance, 0.01)
            assertEquals(0, tripsCount)
        }
        with(wrapper.yearSummary) {
            assertEquals(0, tripsCount)
            assertEquals(0.0, distance, 0.01)
            assertEquals(0, tripsCount)
        }
    }

    @Test
    fun `Correct data for getSummary`() = runTest {
        // Given
        val repository = mockk<BaseRepository>()
        coEvery { repository.fetchYearTracks() } returns listOf(
            TrackDto(null, null, 10000, 19000, 5000.0F),
            TrackDto(null, null, 10000, 20000, 2500.8F),
            TrackDto(null, null, 10000, 250000, 15000.21F),
        )
        coEvery { repository.fetchAllTracks() } returns listOf(
            TrackDto(null, null, 10000, 19000, 5000.0F),
            TrackDto(null, null, 10000, 20000, 2500.8F),
            TrackDto(null, null, 10000, 250000, 15000.21F),
            TrackDto(null, null, 20000, 450000, 25000.21F),
            TrackDto(null, null, 30000, 550000, 105110.21F),
        )
        val useCase = GetSummaryUseCase(repository)

        // When
        val wrapper = useCase.getSummary()

        // Then
        with(wrapper.summary) {
            assertEquals(5, tripsCount)
            assertEquals(152.61, distance, 0.01)
            assertEquals(1209, time)
        }
        with(wrapper.yearSummary) {
            assertEquals(3, tripsCount)
            assertEquals(22.50, distance, 0.01)
            assertEquals(259, time)
        }
    }
}