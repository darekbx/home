package com.darekbx.books.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.books.data.BooksRepository
import com.darekbx.books.data.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    fun fillData() {
        viewModelScope.launch {
            booksRepository.prepareLegacyBooks()
        }
    }

    fun add(book: Book) {
        viewModelScope.launch {
            book.year = Calendar.getInstance().get(Calendar.YEAR)
            booksRepository.add(book)
        }
    }

    fun delete(book: Book?) {
        viewModelScope.launch {
            book?.id?.let {
                booksRepository.deleteBook(it)
            }
        }
    }

    fun update(book: Book) {
        viewModelScope.launch {
            booksRepository.update(book)
        }
    }

    fun books() = booksRepository.getBooks()
}
