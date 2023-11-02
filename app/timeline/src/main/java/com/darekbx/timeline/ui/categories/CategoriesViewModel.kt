package com.darekbx.timeline.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.timeline.repository.TimelineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val timelineRepository: TimelineRepository
) : ViewModel() {

    val categories = timelineRepository.categoriesFlow()

    fun add(name: String, color: Int) {
        viewModelScope.launch {
            timelineRepository.addCategory(name, color)
        }
    }
}