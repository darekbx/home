package com.darekbx.geotracker.ui.trips

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.storage.geotracker.TrackPointsDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchTripsUseCaseTest {

    @Test
    fun `Successfully fetched trips`() = runTest {
        // Given
        val repo = mockk<BaseHomeRepository>()
        val useCase = FetchTripsUseCase(repo)

        coEvery { repo.fetchYearTracks(2023) } returns mockTrips

        // When
        val wrapper = useCase(2023)

        // Then
        assertEquals(4, wrapper.trips.size)
        assert(wrapper.trips.first() is Track)
        assertEquals(wrapper.sumDistance, 25.6325, 0.1)
        assertEquals(wrapper.trips[0].pointsCount, 10)
        assertEquals(wrapper.trips[0].timespan(), "00d 00h 18m")
    }

    private val mockTrips = listOf(
        TrackPointsDto(TrackDto(1L, null, 1694279188471L, 1694280291125L, 4632.5F), 10),
        TrackPointsDto(TrackDto(2L, null, 10000L, null, 10000F), 15),
        TrackPointsDto(TrackDto(3L, null, 21000L, 22000L, 3000F), 20),
        TrackPointsDto(TrackDto(4L, null, 31000L, 32000L, 8000F), 30),
    )
}
