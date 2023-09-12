package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.PointDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMaxSpeedUseCaseTest {

    @Test
    fun `Speed is -1, because table is empty`() = runTest {
        // Given
        val repository = mockk<BaseHomeRepository>()
        coEvery { repository.fetchMaxSpeed() } returns null

        // When
        val speed = GetMaxSpeedUseCase(repository).getMaxSpeed()

        // Then
        assertEquals(-1F, speed, 0.1F)
    }

    @Test
    fun `Speed fetched successfully`() = runTest {
        // Given
        val repository = mockk<BaseHomeRepository>()
        coEvery { repository.fetchMaxSpeed() } returns PointDto(null, 1L, 1L, 0.0, 0.0, 52.2F, 0.0)


        // When
        val speed = GetMaxSpeedUseCase(repository).getMaxSpeed()

        // Then
        assertEquals(187.92F, speed, 0.01F)
    }
}