package com.darekbx.timeline.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.timeline.model.Entry
import com.darekbx.timeline.repository.TimelineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timelineRepository: TimelineRepository
) : ViewModel() {

    val categories = timelineRepository.categoriesFlow()
    val entries = timelineRepository.getEntriesFlow()

    init {
        /*viewModelScope.launch {
            timelineRepository.deleteAll()
        }*/
    }

    fun delete(entry: Entry) {
        viewModelScope.launch {
            timelineRepository.deleteEntry(entry.id)
        }
    }

    fun add(categoryId: Long, title: String, description: String, timestamp: Long) {
        viewModelScope.launch {
            timelineRepository.addEntry(categoryId, title, description, timestamp)
        }
    }
}
