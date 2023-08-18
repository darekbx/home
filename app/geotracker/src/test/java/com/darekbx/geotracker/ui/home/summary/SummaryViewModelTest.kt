package com.darekbx.geotracker.ui.home.summary

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.entities.TrackDto
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SummaryViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `First flow result is null`() = runTest {
        // Given
        val repository = object: BaseHomeRepository {
            override suspend fun fetchAllTracks(): List<TrackDto> {
                return emptyList()
            }

            override suspend fun fetchYearTracks(): List<TrackDto> {
                return emptyList()
            }
        }
        val useCase = GetSummaryUseCase(repository)
        val viewModel = SummaryViewModel(useCase)

        // When
        val summary = viewModel.summary.first()

        // Then
        assertNull(summary)
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
        }
        val useCase = GetSummaryUseCase(repository)
        val viewModel = SummaryViewModel(useCase)

        // When
        val summary = viewModel.summary.first { it != null }

        // Then
        assertNotNull(summary)
        with (summary!!.summary) {
            assertEquals(1, tripsCount)
            assertEquals(5.0, distance, 0.01)
            assertEquals(9, time)
        }
        with (summary!!.yearSummary) {
            assertEquals(1, tripsCount)
            assertEquals(5.0, distance, 0.01)
            assertEquals(9, time)
        }
    }
}