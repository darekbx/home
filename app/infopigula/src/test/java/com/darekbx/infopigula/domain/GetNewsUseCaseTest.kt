package com.darekbx.infopigula.domain

import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.remote.model.FormOptions
import com.darekbx.infopigula.repository.remote.model.NewsResponse
import com.darekbx.infopigula.repository.remote.model.RemoteGroup
import com.darekbx.infopigula.repository.remote.model.RemoteLastRelease
import com.darekbx.infopigula.repository.remote.model.RemotePager
import com.darekbx.infopigula.repository.remote.model.Row
import com.darekbx.infopigula.repository.remote.model.Vote
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNewsUseCaseTest {

    @Test
    fun `Fetched all news`() = runTest {
        // Given
        val remoteRepository = mockk<RemoteRepository>()
        val newsResponse = NewsResponse(
            RemotePager(5, 1, 2, "12"),
            FormOptions(
                listOf(RemoteGroup("2", "Group", "type", "ok")),
                listOf(RemoteLastRelease("2", "Date"))
            ),
            listOf(
                Row(
                    1, 2, "title", 1, 2, 1, 2, "", "", "logo", "ex_link", "ex_link_title", "content", "", "", 1, "", "",
                    Vote(false, null, "10", 5.4)
                )
            )
        )
        coEvery { remoteRepository.getNews(any(), any(), any()) } returns newsResponse

        // When
        val result = GetNewsUseCase(remoteRepository).invoke()

        // Then
        assertTrue(result.isSuccess)
        with(result.getOrThrow()) {
            assertEquals(news[0].title, "title")
            assertEquals(news[0].content, "content")
            assertEquals(news[0].voteCount, "10")
            assertEquals(news[0].voteScore, "5.4")
            assertEquals(news[0].externalLink, "ex_link")
            assertEquals(news[0].externalLinkTitle, "ex_link_title")
            assertEquals(news[0].sourceLogo, "logo")
            assertEquals(pager.page, 1)
            assertEquals(pager.itemsCount, 12)
            assertEquals(pager.pages, 2)
            assertEquals(groups[0].targetId, "2")
            assertEquals(groups[0].value, "Group")
            assertTrue(groups[0].hasAccess)
        }
    }
}
