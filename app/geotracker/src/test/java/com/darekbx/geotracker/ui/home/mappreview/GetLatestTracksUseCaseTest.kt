package com.darekbx.geotracker.ui.home.mappreview

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetLatestTracksUseCaseTest {

    @Test
    fun `Successfully fetched map preview`() = runBlocking {
        // Given
        val homeRepo = mockk<BaseHomeRepository>()
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