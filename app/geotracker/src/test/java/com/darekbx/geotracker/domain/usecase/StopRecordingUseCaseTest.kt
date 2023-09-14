package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StopRecordingUseCaseTest {

    @Test
    fun `Recording was stepped with actual timestamp`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = spyk(StopRecordingUseCase(repo))

        coEvery { repo.updateTrack(any(), any(), any()) } returns Unit
        every { useCase.currentTimestamp() } returns 10000L

        // When
        useCase(40L, "Label")

        // Then
        coVerify { repo.updateTrack(eq(40L), eq(10000L), eq("Label")) }
    }
}