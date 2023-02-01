package com.darekbx.hejto.data

import android.util.Log
import com.darekbx.hejto.data.local.model.FavouriteTag
import com.darekbx.hejto.data.local.model.SavedSlug
import com.darekbx.hejto.data.remote.HejtoService
import com.darekbx.hejto.data.remote.PostComment
import com.darekbx.hejto.data.remote.PostDetails
import com.darekbx.hejto.data.remote.ResponseWrapper
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter
import com.darekbx.storage.hejto.FavouriteTagDto
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.hejto.SavedSlugDto
import javax.inject.Inject

class HejtoRespoitory @Inject constructor(
    private val hejtoService: HejtoService,
    private val hejtoDao: HejtoDao
) {

    suspend fun getSavedSlugs(): List<SavedSlug> {
        return hejtoDao.listSavedSlugs().map {
            SavedSlug(it.slug, it.title, it.content)
        }
    }

    suspend fun saveSlug(savedSlug: SavedSlug) {
        with(savedSlug) {
            hejtoDao.add(SavedSlugDto(null, slug, title, contents))
        }
    }

    suspend fun removeSlug(slug: String) {
        hejtoDao.removeSavedSlug(slug)
    }

    suspend fun updateEntriesCount(name: String, entriesCount: Int) {
        hejtoDao.update(name, entriesCount)
    }

    suspend fun addRemoveFavouriteTag(name: String) {
        if (hejtoDao.contains(name) != 0) {
            hejtoDao.remove(name)
        } else {
            hejtoDao.add(FavouriteTagDto(null, name))
        }
    }

    suspend fun getFavouriteTags(): List<FavouriteTag> {
        return hejtoDao.listAll().map {
            FavouriteTag(it.name, it.entriesCount, 0)
        }
    }

    suspend fun getPostComments(page: Int, slug: String): ResponseWrapper<PostComment> {
        return hejtoService.getPostComments(slug, page)
    }

    suspend fun getPosts(
        page: Int,
        tag: String? = null,
        communitySlug: String? = null,
        periodFilter: PeriodFilter,
        order: Order
    ): ResponseWrapper<PostDetails> {
        val period = periodFilter.filter
        val orderQuery = order.order
        val tags = tag?.let { listOf(it) }
        return hejtoService
            .getPosts(
                tags = tags,
                community = communitySlug,
                period = period,
                orderBy = orderQuery,
                page = page
            )
    }

    suspend fun getTag(name: String) =
        hejtoService.getTag(name)

    suspend fun getTags(page: Int, limit: Int) =
        hejtoService.getTags(page, limit)

    suspend fun getPostDetails(slug: String) =
        hejtoService.getPostDetails(slug)

    suspend fun getCommunities(page: Int) =
        hejtoService.getCommunities(page, orderBy = "numPosts")
}
