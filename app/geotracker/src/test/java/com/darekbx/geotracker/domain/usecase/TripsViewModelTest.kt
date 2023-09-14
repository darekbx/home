package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.MainCoroutineRule
import com.darekbx.geotracker.ui.trips.viewmodels.TripsUiState
import com.darekbx.geotracker.ui.trips.viewmodels.TripsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripsViewModelTest {

    @get:Rule
    var coroutinesTestRule = MainCoroutineRule()

    @Test
    fun `Load trips`() = runTest {
        // Given
        val tripsUseCase = mockk<FetchTripsUseCase>()
        val fixEndTimestampUseCase = mockk<FixEndTimestampUseCase>()
        val deleteTrackUseCase = mockk<DeleteTrackUseCase>()
        coEvery { tripsUseCase.invoke(2023) } returns TripsWrapper(10.0, emptyList())

        val testViewModel = TripsViewModel(tripsUseCase, fixEndTimestampUseCase, deleteTrackUseCase)

        // When
        testViewModel.loadTrips(2023)
        advanceUntilIdle()

        // Then
        val uiState= testViewModel.uiState.first()
        assert(uiState is TripsUiState.Done)
        assert((uiState as TripsUiState.Done).data.trips.isEmpty())
        assert(uiState.data.sumDistance == 10.0)
    }
}