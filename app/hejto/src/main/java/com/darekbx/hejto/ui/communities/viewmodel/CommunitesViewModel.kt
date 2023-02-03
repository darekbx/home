package com.darekbx.hejto.ui.communities.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.BuildConfig
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.remote.Community
import com.darekbx.hejto.data.remote.HejtoService
import com.darekbx.hejto.data.remote.Items
import com.darekbx.hejto.data.remote.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class CommunitesViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    val communities = Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
        CommonPagingSource { page ->
            _uiState.value = UiState.InProgress
            try {
                val data = hejtoRespoitory.getCommunities(page)
                data.contents.items.forEach {
                    val prevCommunityPosts = hejtoRespoitory.getCommunityPosts(it.slug)
                    it.previousPostsCount = prevCommunityPosts ?: it.postsCount
                }
                _uiState.value = UiState.Idle
                return@CommonPagingSource data
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
            return@CommonPagingSource ResponseWrapper(0, 0, Items(emptyList<Community>()))
        }
    }.flow

    fun updateCommunityInfo(slug: String, postsCount: Int) {
        viewModelScope.launch {
            hejtoRespoitory.updateCommunityInfo(slug, postsCount)
        }
    }
}
