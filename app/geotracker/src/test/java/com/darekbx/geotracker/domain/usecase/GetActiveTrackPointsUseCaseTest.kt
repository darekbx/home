package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.model.Point
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetActiveTrackPointsUseCaseTest {

    @Test
    fun `Empty flow when thres no unfinshed track`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = GetActiveTrackPointsUseCase(repo)
        coEvery { repo.fetchLivePoints() } returns emptyFlow()

        // When
        val points = useCase.invoke().firstOrNull()

        // Then
        assertNull(points)
    }

    @Test
    fun `Collected track points`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = GetActiveTrackPointsUseCase(repo)
        coEvery { repo.fetchLivePoints() } returns testFlow()

        // When
        val points = useCase.invoke().first()

        // Then
        assertEquals(2, points.size)
        assertTrue(points.first() is Point)
        with(points.first()) {
            assertEquals(412512L, timestamp)
            assertEquals(52.12, latitude, 0.1)
            assertEquals(21.38, longitude, 0.1)
            assertEquals(452F, speed)
            assertEquals(106.0, altitude, 0.1)
        }
    }

    private fun testFlow() = flow {
        emit(listOf(
            PointDto(2L, 10L, 412512L, 52.12, 21.38, 452F, 106.0),
            PointDto(1L, 10L, 412412L, 52.1, 21.3, 412F, 105.0),
        ))
    }
}