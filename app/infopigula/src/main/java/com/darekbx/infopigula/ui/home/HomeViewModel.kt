package com.darekbx.infopigula.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.GetNewsUseCase
import com.darekbx.infopigula.domain.GetNewsUseCase.Companion.CREATORS_GROUP
import com.darekbx.infopigula.model.Group
import com.darekbx.infopigula.model.LastRelease
import com.darekbx.infopigula.model.News
import com.darekbx.infopigula.repository.Session
import com.darekbx.infopigula.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Idle : HomeUiState()
    object InProgress : HomeUiState()
    object Done : HomeUiState()
    class Failed(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val settingsRepository: SettingsRepository,
    private val session: Session
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: Flow<HomeUiState>
        get() = _uiState

    var groups = mutableStateListOf<Group>()
    var news = mutableStateListOf<News>()
    var lastReleases = mutableStateListOf<LastRelease>()
    var hasNextPage = true

    init {
        viewModelScope.launch {
            /**
             * Listen for sesison changes.
             * When user is authorized, fetch news
             */
            session.isUserActive.consumeEach { isActive ->
                if (isActive) {
                    // Commented out to prevent duplicated news
                    //loadNews()
                }
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Idle
        }
    }

    fun loadNews(
        groupId: Int = GetNewsUseCase.DEFAULT_GROUP,
        page: Int = 0,
        lastReleaseId: Int? = null
    ) {
        viewModelScope.launch {
            // Always clear news list when page is zero, to avoid duplications
            if (page == 0) {
                news.clear()
            }

            _uiState.value = HomeUiState.InProgress

            val filteredGroups = settingsRepository.filteredGroups()
            val result = getNewsUseCase
                .invoke(groupId, page, lastReleaseId == null, lastReleaseId)

            if (result.isSuccess) {
                result.getOrNull()
                    ?.let { newsWrapper ->
                        val groupsFiltered = newsWrapper.groups
                            .filter { !filteredGroups.contains(it.targetId) }
                            .toMutableList()

                        // Creators are no a part of a Group, but behaves like a group
                        addCreators(groupsFiltered)

                        lastReleases.replace(newsWrapper.releases)
                        groups.replace(groupsFiltered)

                        news.addAll(newsWrapper.news)

                        hasNextPage = (page + 1) < newsWrapper.pager.pages
                        _uiState.value = HomeUiState.Done
                    }
                    ?: run { _uiState.value = HomeUiState.Failed("Data is empty!") }
            } else {
                _uiState.value =
                    HomeUiState.Failed(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    private fun addCreators(groupsFiltered: MutableList<Group>) {
        groupsFiltered.add(Group(CREATORS_GROUP, "Twórcy", true))
    }

    private fun <T> SnapshotStateList<T>.replace(elements: Collection<T>) {
        clear()
        addAll(elements)
    }
}