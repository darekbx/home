package com.darekbx.hejto.data

import com.darekbx.hejto.data.remote.HejtoService
import com.darekbx.hejto.data.remote.PostDetails
import com.darekbx.hejto.data.remote.ResponseWrapper
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter
import javax.inject.Inject

class HejtoRespoitory @Inject constructor(
    private val hejtoService: HejtoService
) {

    suspend fun getCommunityCategories(page: Int) =
        hejtoService.getCommunityCategories(page)

    suspend fun getCommunityPosts(community: String, page: Int) =
        hejtoService.getPosts(null, null, null, null, null, page)

    suspend fun getPosts(
        page: Int,
        periodFilter: PeriodFilter,
        order: Order
    ): ResponseWrapper<PostDetails> {
        val period = periodFilter.filter
        val orderQuery = order.order
        return hejtoService.getPosts(period = period, orderBy = orderQuery, page = page)
    }

    suspend fun getTags(page: Int) =
        hejtoService.getTags(page)

    suspend fun getPostDetails(slug: String) =
        hejtoService.getPostDetails(slug)
}
