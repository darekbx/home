package com.darekbx.rssreader.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.rssreader.BuildConfig
import com.darekbx.rssreader.data.NewsRepository
import com.darekbx.rssreader.data.model.NewsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class Progress(val name: String, val progress: Double)

sealed class UiState {
    class InProgress(val progress: Progress) : UiState()
    class Done(val items: List<NewsItem>) : UiState()
    class Error(val message: String) : UiState()
    object Idle : UiState()
}

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    fun loadFeed() {
        viewModelScope.launch {
            try {
                val items = newsRepository.loadAll { name, progress ->
                    _uiState.value = UiState.InProgress(Progress(name, progress))
                }
                _uiState.value = UiState.Done(items)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
