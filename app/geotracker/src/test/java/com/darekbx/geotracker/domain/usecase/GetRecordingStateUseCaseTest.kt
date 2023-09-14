package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetRecordingStateUseCaseTest {

    @Test
    fun `There is no active recording`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        coEvery { repo.fetchUnFinishedTracks() } returns emptyList()

        val useCase = GetRecordingStateUseCase(repo)

        // When
        val result = useCase()

        // Then
        assertNull(result)
    }

    @Test
    fun `There is active recording`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        coEvery { repo.fetchUnFinishedTracks() } returns listOf(mockk())

        val useCase = GetRecordingStateUseCase(repo)

        // When
        val result = useCase()

        // Then
        assertNotNull(result)
    }
}
