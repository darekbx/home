package com.darekbx.geotracker.domain.usecase

import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.geotracker.repository.model.Track
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetTrackWithPointsTest {

    @Test
    fun `Fetched track without points`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = GetTrackWithPoints(repo)

        val track = TrackDto(1L, "label", 10L, 20L, 100F)

        coEvery { repo.fetch(any()) } returns track
        coEvery { repo.fetchTrackPoints(any()) } returns emptyList()

        // When
        val data = useCase.invoke(1L)

        // Then
        assert(data.track is Track)
        with (data.track) {
            assert(id == 1L)
            assert(label == "label")
            assert(startTimestamp == 10L)
            assert(endTimestamp == 20L)
            assert(distance == 100F)
            assert(pointsCount == 0)
        }
        assert(data.points.isEmpty())
    }

    @Test
    fun `Fetched track with points`() = runTest {
        // Given
        val repo = mockk<BaseRepository>()
        val useCase = GetTrackWithPoints(repo)

        val track = TrackDto(1L, "label", 10L, 20L, 100F)
        val point = PointDto(2L, 1L, 10L, 10.0, 20.0, 40F, 100.0)

        coEvery { repo.fetch(any()) } returns track
        coEvery { repo.fetchTrackPoints(any()) } returns listOf(point)

        // When
        val data = useCase.invoke(1L)

        // Then
        assert(data.track is Track)
        with (data.track) {
            assert(id == 1L)
            assert(label == "label")
            assert(startTimestamp == 10L)
            assert(endTimestamp == 20L)
            assert(distance == 100F)
            assert(pointsCount == 1)
        }
        assert(data.points.isNotEmpty())
        with(data.points.get(0)) {
            assert(timestamp == 10L)
            assert(latitude == 10.0)
            assert(longitude == 20.0)
            assert(speed == 40F)
            assert(altitude == 100.0)
        }
    }
}