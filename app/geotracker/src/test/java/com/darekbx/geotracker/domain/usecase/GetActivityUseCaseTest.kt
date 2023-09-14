package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActivityUseCaseTest {

    @Test
    fun `Activity data successfully fetched`() = runTest {
        // Given
        val repository = mockk<BaseRepository>()
        coEvery { repository.fetchYearTracks() } returns listOf(
            TrackDto(null, null, 1672579053, 1, 5000.0F),
            TrackDto(null, null, 1672599053, 1, 5000.0F),
            TrackDto(null, null, 1672665453, 1, 2500.8F),
            TrackDto(null, null, 1672751853, 1, 15000.21F)
        )

        // When
        val activity = GetActivityUseCase(repository).getActivityData()

        // Then
        assertEquals(3, activity.size)
        assertEquals(10000.0, activity[0].sumDistance(), 0.01)
        assertEquals(2, activity[0].distances.size)
        assertEquals(2500.80, activity[1].sumDistance(), 0.01)
        assertEquals(15000.20, activity[2].sumDistance(), 0.01)
    }
}