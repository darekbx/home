package com.darekbx.hejto.ui.communities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.remote.HejtoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunitesViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    val communities = Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
        CommonPagingSource { page ->
            val data = hejtoRespoitory.getCommunities(page)
            data.contents.items.forEach {
                val prevCommunityPosts = hejtoRespoitory.getCommunityPosts(it.slug)
                it.previousPostsCount = prevCommunityPosts ?: it.postsCount
            }
            data
        }
    }.flow

    fun updateCommunityInfo(slug: String, postsCount: Int) {
        viewModelScope.launch {
            hejtoRespoitory.updateCommunityInfo(slug, postsCount)
        }
    }
}
