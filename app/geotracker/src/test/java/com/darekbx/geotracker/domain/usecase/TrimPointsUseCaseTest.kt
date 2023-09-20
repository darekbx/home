package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.model.Point
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TrimPointsUseCaseTest {

    @Test
    fun `Points were trimmed`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = TrimPointsUseCase(repo)

        coEvery { repo.deleteAllPoints(any()) } returns Unit
        coEvery { repo.add(any<List<PointDto>>()) } returns Unit

        // When
        useCase(1L, testPoints())

        // Then
        coVerify { repo.deleteAllPoints(any()) }
        coVerify { repo.add(any<List<PointDto>>()) }
    }

    private fun testPoints() =
        listOf(
            Point(412512L, 52.12, 21.38, 452F, 106.0),
            Point(412412L, 52.1, 21.31, 412F, 155.0),
            Point(412412L, 52.13, 21.3, 418F, 105.0)
        )
}