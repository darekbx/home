package com.darekbx.books.ui.toread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.books.data.BooksRepository
import com.darekbx.books.data.model.ToRead
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToReadViewModel @Inject constructor(
    private val booksRepository: BooksRepository
    ) :
    ViewModel() {

    fun fillData() {
        viewModelScope.launch {
            booksRepository.prepareLegacyToRead()
        }
    }

    fun add(toRead: ToRead) {
        viewModelScope.launch {
            booksRepository.add(toRead)
        }
    }

    fun delete(toRead: ToRead?) {
        viewModelScope.launch {
            toRead?.id?.let {
                booksRepository.deleteToRead(it)
            }
        }
    }

    fun items() = booksRepository.getToRead()
}
