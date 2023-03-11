package com.darekbx.notepad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.notepad.data.NotepadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotepadViewModel @Inject constructor(
    private val notepadRepository: NotepadRepository
) : ViewModel() {

    fun getNotes() = flow {
        emit(notepadRepository.getNotes())
    }

    fun updateNote(id: Long, contents: String) {
        viewModelScope.launch {
            notepadRepository.updateNote(id, contents)
        }
    }
}
