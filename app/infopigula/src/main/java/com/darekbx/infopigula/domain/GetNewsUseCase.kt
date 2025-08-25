package com.darekbx.infopigula.domain

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.model.Group
import com.darekbx.infopigula.model.LastRelease
import com.darekbx.infopigula.model.News
import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.model.NewsWrapper
import com.darekbx.infopigula.model.Pager
import com.darekbx.infopigula.model.SingleNews
import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.remote.model.RemoteGroup
import com.darekbx.infopigula.repository.remote.model.RemoteLastRelease
import com.darekbx.infopigula.repository.remote.model.RemotePager
import com.darekbx.infopigula.repository.remote.model.Row
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    suspend operator fun invoke(
        groupId: Int = DEFAULT_GROUP,
        page: Int = 0,
        showLastRelease: Boolean = true,
        releaseId: Int? = null
    ): Result<NewsResponse> {
        try {
            val latestReleaseFlag = if (showLastRelease) 1 else 0
            val response = if (groupId == CREATORS_GROUP) {
                remoteRepository.getCreators(page = 0, latestReleaseFlag)
            } else {
                remoteRepository.getNews()
            }
            return Result.success(response)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return Result.failure(e)
        }
    }

    @Deprecated("Old api")
    private fun List<Row>.mapAllRows() = map { row ->
        with(row) {
            News(
                titleNews,
                sourceLogoImg,
                sourceExternalLink,
                sourceExternalLinkOverride,
                fieldNewsContent,
                vote.voteCount,
                "${vote.voteAverage}"
            )
        }
    }

    @Deprecated("Old api")
    private fun List<RemoteGroup>.mapAllGroups() = map { group ->
        Group(group.targetId.toInt(), group.value, group.access == "ok")
    }

    @Deprecated("Old api")
    private fun List<RemoteLastRelease>.mapAllLastReleases() = map { release ->
        LastRelease(release.targetId.toInt(), release.value)
    }

    @Deprecated("Old api")
    private fun RemotePager.toPager() =
        Pager(this.currentPage, this.totalPages, this.totalItems.toIntOrNull() ?: -1)

    companion object {
        const val DEFAULT_GROUP = 2
        const val CREATORS_GROUP = -100
    }
}