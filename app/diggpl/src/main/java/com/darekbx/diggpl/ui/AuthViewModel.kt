package com.darekbx.diggpl.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.diggpl.data.WykopRepository
import com.darekbx.diggpl.data.remote.ResponseResult
import com.darekbx.diggpl.data.remote.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    var tagStream = mutableStateListOf<Tag>()
    var hasNextPage = true

    /**
     * @return True if next page is available
     */
    fun loadTags(tagName: String, page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            val result = wykopRepository.getTags(tagName, page)
            when (result) {
                is ResponseResult.Success -> {
                    val pagination = result.data.pagination
                    val items = result.data.data
                    hasNextPage = page < pagination.total
                    tagStream.addAll(items)
                    _uiState.value = UiState.Idle
                }
                is ResponseResult.Failure -> {
                    _uiState.value = UiState.Error(result.error.message ?: "Unknown error")
                }
            }
        }
    }
}
