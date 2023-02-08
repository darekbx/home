package com.darekbx.lifetimememo.screens.memos.viewmodel

import androidx.lifecycle.*
import com.darekbx.lifetimememo.screens.memos.MemosUiState
import com.darekbx.lifetimememo.screens.memos.model.Container
import com.darekbx.lifetimememo.screens.memos.model.Location
import com.darekbx.lifetimememo.screens.memos.model.Memo
import com.darekbx.lifetimememo.screens.memos.repository.MemosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MemosViewModel @Inject constructor(
    private val memosRepository: MemosRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<MemosUiState>(MemosUiState.InProgress)
    val uiState: LiveData<MemosUiState>
        get() = _uiState

    suspend fun getPath(parentId: String?): List<String> {
        val path = memosRepository.getPath(parentId)
        return path.reversed()
    }

    fun getContainer(parentId: String?): LiveData<Container> {
        if (parentId != null) {
            return memosRepository.getContainer(parentId).asLiveData()
        } else {
            return liveData { }
        }
    }

    fun getLocation(memoId: String?): LiveData<Location?> {
        if (memoId != null) {
            return memosRepository.getLocation(memoId).asLiveData()
        } else {
            return liveData { }
        }
    }

    suspend fun getMemo(id: String?): Memo? {
        return memosRepository.getMemo(id)
    }

    fun elements(parentId: String?): LiveData<List<Any>> =
        memosRepository.elements(containerId = parentId)
            .onEach { _uiState.value = MemosUiState.Done }
            .asLiveData()

    fun update(
        memoId: String,
        title: String,
        shortInfo: String,
        description: String,
        categoryId: String,
        flag: Int?,
        link: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = MemosUiState.InProgress
            val memo = memosRepository.getMemo(memoId) ?: return@launch
            memosRepository.update(
                Memo(memo.uid, memo.parentUid, title, memo.timestamp,
                    categoryId, shortInfo, description, link, flag = flag)
            )
        }
    }

    fun addLocation(memoId: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            memosRepository.add(Location(UUID.randomUUID().toString(),memoId, lat, lng))
        }
    }

    fun updateLocation(memoId: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            memosRepository.update(memoId, lat, lng)
        }
    }

    fun add(
        title: String,
        shortInfo: String,
        description: String,
        categoryId: String,
        flag: Int?,
        link: String? = null,
        parentId: String? = null
    ): String {
        val newRowUid = UUID.randomUUID().toString()
        viewModelScope.launch {
            _uiState.value = MemosUiState.InProgress
            memosRepository.add(
                Memo(
                    newRowUid,
                    parentId,
                    title,
                    System.currentTimeMillis(),
                    categoryId,
                    shortInfo,
                    description,
                    link,
                    flag = flag
                )
            )
        }
        return newRowUid
    }

    fun add(
        title: String,
        subtitle: String,
        parentId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = MemosUiState.InProgress
            memosRepository.add(
                Container(
                    UUID.randomUUID().toString(),
                    parentId,
                    title,
                    subtitle
                )
            )
        }
    }

    fun delete(container: Container) {
        viewModelScope.launch {
            memosRepository.delete(container)
        }
    }

    fun delete(memo: Memo) {
        viewModelScope.launch {
            memosRepository.delete(memo)
        }
    }
}
