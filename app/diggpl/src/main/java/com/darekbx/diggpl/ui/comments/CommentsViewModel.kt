package com.darekbx.diggpl.ui.comments

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.diggpl.data.WykopRepository
import com.darekbx.diggpl.data.remote.Comment
import com.darekbx.diggpl.data.remote.ResponseResult
import com.darekbx.diggpl.ui.homepage.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    var commentItems = mutableStateListOf<Comment>()
    var hasNextPage = true

    fun loadComments(linkId: Int? = null, entryId: Int? = null, page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress

            val result = when {
                linkId != null -> wykopRepository.getLinkComments(linkId, page = page)
                entryId != null -> wykopRepository.getEntryComments(entryId, page = page)
                else -> return@launch
            }

            when (result) {
                is ResponseResult.Success -> {
                    val pagination = result.data.pagination
                    val items = result.data.data
                    hasNextPage = page < pagination.total
                    commentItems.addAll(items)
                    _uiState.value = UiState.Idle
                }
                is ResponseResult.Failure -> {
                    _uiState.value = UiState.Error(result.error.message ?: "Unknown error")
                }
            }
        }
    }
}
