package com.darekbx.geotracker.domain.usecase

import android.location.Location
import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddLocationUseCaseTest {

    @Test
    fun `Create new track`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = AddLocationUseCase(repo)

        val location = mockLocation()

        coEvery { repo.fetchUnFinishedTracks() } returns emptyList()
        coEvery { repo.add(any<TrackDto>()) } returns 1L
        coEvery { repo.add(any<PointDto>()) } returns 1L

        // When
        useCase(location)

        // Then
        coVerify(exactly = 1) { repo.add(any<TrackDto>()) }
        coVerify(exactly = 1) { repo.add(any<PointDto>()) }
    }

    @Test
    fun `Add points to track`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = AddLocationUseCase(repo)

        val trackDto = mockk<TrackDto>()
        every { trackDto.id } returns 1L

        val location = mockLocation()

        coEvery { repo.fetchUnFinishedTracks() } returns listOf(trackDto)
        coEvery { repo.add(any<TrackDto>()) } returns 1L
        coEvery { repo.add(any<PointDto>()) } returns 1L

        // When
        useCase(location)

        // Then
        coVerify(exactly = 0) { repo.add(any<TrackDto>()) }
        coVerify(exactly = 1) { repo.add(any<PointDto>()) }
    }

    private fun mockLocation(): Location {
        val location = mockk<Location>()
        every { location.latitude } returns 1.0
        every { location.longitude } returns 1.0
        every { location.speed } returns 1.0F
        every { location.altitude } returns 1.0
        return location
    }
}