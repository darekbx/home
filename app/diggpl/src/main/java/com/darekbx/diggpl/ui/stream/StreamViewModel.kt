package com.darekbx.diggpl.ui.stream

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.diggpl.data.WykopRepository
import com.darekbx.diggpl.data.remote.ResponseResult
import com.darekbx.diggpl.data.remote.StreamItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    var streamItems = mutableStateListOf<StreamItem>()
    var hasNextPage = true

    /**
     * @return True if next page is available
     */
    fun loadTags(tagName: String, page: Int = 1, clear: Boolean = false) {
        viewModelScope.launch {
            if (clear) {
                streamItems.clear()
            }
            _uiState.value = UiState.InProgress
            val result = wykopRepository.getTags(tagName, page = page)

            when (result) {
                is ResponseResult.Success -> {
                    // Mark tag as opened
                    if (page == 1 && result.data.data.isNotEmpty()) {
                        val newestDate = result.data.data.maxBy { it.date }.date
                        markOpenedTag(tagName, newestDate)
                    }
                    val pagination = result.data.pagination
                    val items = result.data.data
                    hasNextPage = page < pagination.total
                    streamItems.addAll(items)
                    _uiState.value = UiState.Idle
                }
                is ResponseResult.Failure -> {
                    _uiState.value = UiState.Error(result.error.message ?: "Unknown error")
                }
            }
        }
    }

    private fun markOpenedTag(name: String, newestTagDate: String) {
        viewModelScope.launch {
            wykopRepository.updateTagLastId(name, newestTagDate)
        }
    }

}
