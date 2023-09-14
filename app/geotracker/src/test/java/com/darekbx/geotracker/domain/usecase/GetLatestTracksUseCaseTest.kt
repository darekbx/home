package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetLatestTracksUseCaseTest {

    @Test
    fun `Successfully fetched map preview`() = runTest {
        // Given
        val homeRepo = mockk<BaseRepository>()
        val settingsRepo = mockk<SettingsRepository>()

        coEvery { homeRepo.fetchYearTrackPoints(any()) } returns emptyMap()
        coEvery { settingsRepo.nthPointsToSkip() } returns 4

        val useCase = GetLatestTracksUseCase(homeRepo, settingsRepo)

        // When
        val result = useCase()

        // Then
        assert(result.isEmpty())
        coVerify(exactly = 1) { settingsRepo.nthPointsToSkip() }
        coVerify(exactly = 1) { homeRepo.fetchYearTrackPoints(eq(4)) }
    }
}