package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FixEndTimestampUseCaseTest {

    @Test
    fun `Dont fix endtimestamp when is filled`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = FixEndTimestampUseCase(repo)

        coEvery { repo.fetch(1L) } returns TrackDto(1L, null, 10L, 20L)
        coEvery { repo.update(any(), any()) } returns Unit

        // When
        useCase.invoke(1L)

        // Then
        coVerify(exactly = 0) { repo.update(any(), any()) }
    }

    @Test
    fun `Sucessfully fixed endtimestamp`() = runTest {
        // Given
        val oneHour = 60 * 60 * 1000
        val repo = mockk<BaseRepository>()
        val useCase = FixEndTimestampUseCase(repo)

        coEvery { repo.fetch(1L) } returns TrackDto(1L, null, 10L, null)
        coEvery { repo.update(any(), any()) } returns Unit

        // When
        useCase.invoke(1L)

        // Then
        coVerify(exactly = 1) { repo.update(eq(1L), eq(10L + oneHour)) }
    }
}