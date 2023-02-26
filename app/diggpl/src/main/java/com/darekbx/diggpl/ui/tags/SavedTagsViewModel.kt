package com.darekbx.diggpl.ui.tags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.diggpl.BuildConfig
import com.darekbx.diggpl.data.WykopRepository
import com.darekbx.diggpl.data.local.model.SavedTag
import com.darekbx.diggpl.data.remote.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
    class Error(val message: String) : UiState()
}

@HiltViewModel
class SavedTagsViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    val savedTags = mutableStateListOf<SavedTag>()

    fun tagAutocomplete(query: String) = flow {
        emit(wykopRepository.tagAutocomplete(query))
    }

    fun removeSavedTag(name: String) {
        viewModelScope.launch {
            wykopRepository.addRemoveSavedTag(name)
            savedTags.removeIf { it.name == name }
        }
    }

    fun getSavedTags() = flow {
        emit(wykopRepository.getSavedTags())
    }

    fun addRemoveSavedTag(name: String) {
        viewModelScope.launch {
            wykopRepository.addRemoveSavedTag(name)
        }
    }

    fun loadSavedTags() {
        viewModelScope.launch {
            _uiState.value = UiState.InProgress
            savedTags.clear()
            try {
                wykopRepository.getSavedTags().forEach { localTag ->
                    val remoteTagInfo = wykopRepository.getTagNewCount(localTag.name, localTag.lastDate)
                    if (remoteTagInfo is ResponseResult.Success) {
                        localTag.newEntriesCount = remoteTagInfo.data.data.count
                        savedTags.add(localTag)
                    }
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
}
