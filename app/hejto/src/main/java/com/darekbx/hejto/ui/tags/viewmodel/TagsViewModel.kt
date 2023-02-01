package com.darekbx.hejto.ui.tags.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.BuildConfig
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.local.model.FavouriteTag
import com.darekbx.hejto.data.remote.HejtoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    val favouriteTags = mutableStateListOf<FavouriteTag>()

    fun removeFavouriteTag(name: String) {
        viewModelScope.launch {
            hejtoRespoitory.addRemoveFavouriteTag(name)
            favouriteTags.removeIf { it.name == name }
        }
    }

    fun getFavouriteTags() = flow {
        emit(hejtoRespoitory.getFavouriteTags())
    }

    fun markOpenedTag(name: String, entriesCount: Int) {
        viewModelScope.launch {
            hejtoRespoitory.updateEntriesCount(name, entriesCount)
        }
    }

    fun addRemoveFavouriteTag(name: String) {
        viewModelScope.launch {
            hejtoRespoitory.addRemoveFavouriteTag(name)
        }
    }

    fun loadFavouritesTags() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            favouriteTags.clear()
            try {
                hejtoRespoitory.getFavouriteTags().forEach { localTag ->
                    val remoteTag = hejtoRespoitory.getTag(localTag.name)
                    val remotePostsCount = remoteTag.postsCount
                    localTag.newEntriesCount =  max(0, remotePostsCount - localTag.entriesCount)
                    localTag.entriesCount = remoteTag.postsCount
                    favouriteTags.add(localTag)
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }

            _uiState.value = UiState.Idle
        }
    }

    val tags =
        Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
            CommonPagingSource { page ->
                hejtoRespoitory.getTags(page, HejtoService.PAGE_SIZE)
            }
        }.flow
}
