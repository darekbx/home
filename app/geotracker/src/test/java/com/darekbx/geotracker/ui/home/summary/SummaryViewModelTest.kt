package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SummaryViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `First flow result is Idle`() = runTest {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchMaxSpeed(): PointDto? = null
        }
        val useCase = GetSummaryUseCase(repository)
        val maxSpeedUseCase = GetMaxSpeedUseCase(repository)
        val viewModel = SummaryViewModel(useCase, maxSpeedUseCase)

        // When
        val summary = viewModel.uiState.firstOrNull()

        // Then
        assertTrue(summary is SummaryUiState.Idle)
    }

    @Test
    fun `Summary was loaded on init`() = runTest {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return listOf(TrackDto(null, null, 10000, 19000, 5000.0F))
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return listOf(TrackDto(null, null, 10000, 19000, 5000.0F))
            }

            override suspend fun fetchMaxSpeed(): PointDto =
                PointDto(null, 1L, 1L, 0.0, 0.0, 52.2F, 0.0)
        }
        val useCase = GetSummaryUseCase(repository)
        val maxSpeedUseCase = GetMaxSpeedUseCase(repository)
        val viewModel = SummaryViewModel(useCase, maxSpeedUseCase)

        // When
        val state = viewModel.uiState.firstOrNull { it is SummaryUiState.Done }

        // Then
        assertNotNull(state)
        with ((state as SummaryUiState.Done).data.summary) {
            assertEquals(1, tripsCount)
            assertEquals(5.0, distance, 0.01)
            assertEquals(9, time)
        }
        with (state.data.yearSummary) {
            assertEquals(1, tripsCount)
            assertEquals(5.0, distance, 0.01)
            assertEquals(9, time)
        }
        assertEquals(187.92F, state.maxSpeed)
    }
}