package com.darekbx.lifetimememo.screens.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.darekbx.lifetimememo.screens.search.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    fun searchInElements(query: String): LiveData<List<Any>> {
        if (query.isBlank()) {
            return emptyFlow<List<Any>>().asLiveData()
        }
        return searchRepository.searchInMemos(query)
            .zip(searchRepository.searchInContainers(query)) { memos, containers ->
                memos + containers
            }
            .asLiveData()
    }
}