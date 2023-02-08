package com.darekbx.lifetimememo.screens.category.viewmodel

import androidx.lifecycle.*
import com.darekbx.lifetimememo.screens.category.CategoryUiState
import com.darekbx.lifetimememo.screens.category.model.Category
import com.darekbx.lifetimememo.screens.category.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<CategoryUiState>(CategoryUiState.InProgress)
    val uiState: LiveData<CategoryUiState>
        get() = _uiState

    val categories: LiveData<List<Category>> =
        categoryRepository
            .categories()
            .onStart { delay(500L) }
            .onEach { _uiState.value = CategoryUiState.Done }
            .asLiveData()

    fun delete(uid: String) {
        viewModelScope.launch {
            categoryRepository.delete(uid)
        }
    }

    fun add(name: String) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.InProgress
            categoryRepository.add(
                Category(UUID.randomUUID().toString(), name)
            )
        }
    }
}
