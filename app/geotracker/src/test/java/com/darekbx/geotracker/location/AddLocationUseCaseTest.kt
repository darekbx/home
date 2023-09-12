package com.darekbx.geotracker.location

import android.location.Location
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.TrackDao
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
        val pointDao = mockk<PointDao>()
        val trackDao = mockk<TrackDao>()
        val useCase = AddLocationUseCase(pointDao, trackDao)

        val location = mockLocation()

        coEvery { trackDao.fetchUnFinishedTracks() } returns emptyList()
        coEvery { pointDao.add(any()) } returns Unit
        coEvery { trackDao.add(any()) } returns 1L

        // When
        useCase(location)

        // Then
        coVerify(exactly = 1) { trackDao.add(any()) }
        coVerify(exactly = 1) { pointDao.add(any()) }
    }

    @Test
    fun `Add points to track`() = runTest {
        // Given
        val pointDao = mockk<PointDao>()
        val trackDao = mockk<TrackDao>()
        val useCase = AddLocationUseCase(pointDao, trackDao)

        val trackDto = mockk<TrackDto>()
        every { trackDto.id } returns 1L

        val location = mockLocation()

        coEvery { trackDao.fetchUnFinishedTracks() } returns listOf(trackDto)
        coEvery { pointDao.add(any()) } returns Unit
        coEvery { trackDao.add(any()) } returns 1L

        // When
        useCase(location)

        // Then
        coVerify(exactly = 0) { trackDao.add(any()) }
        coVerify(exactly = 1) { pointDao.add(any()) }
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