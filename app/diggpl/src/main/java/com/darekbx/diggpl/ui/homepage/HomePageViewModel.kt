package com.darekbx.diggpl.ui.homepage

import android.util.Log
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
class HomePageViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    var linkStreamItems = mutableStateListOf<StreamItem>()
    var hasNextPage = true

    /**
     * @return True if next page is available
     */
    fun loadHomepage(page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            when (val result = wykopRepository.getLinks(TYPE_HOMEPAGE, page = page)) {
                is ResponseResult.Success -> {
                    val pagination = result.data.pagination
                    val items = result.data.data
                    hasNextPage = page < pagination.total

                    // API response for different pages sometimes contains the same entries
                    // That's we need to filter out duplicates...
                    items.forEach { newItem ->
                        if (linkStreamItems.count { it.slug == newItem.slug } == 0) {
                            linkStreamItems.add(newItem)
                        } else {
                            Log.v(TAG, "Filtered out duplicate: ${newItem.slug}")
                        }
                    }

                    _uiState.value = UiState.Idle
                }
                is ResponseResult.Failure -> {
                    _uiState.value = UiState.Error(result.error.message ?: "Unknown error")
                }
            }
        }
    }

    companion object {
        private const val TAG = "HomePageViewModel"
        private const val TYPE_HOMEPAGE = "homepage"
    }
}
