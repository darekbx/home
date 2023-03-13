package com.darekbx.books.data

import com.darekbx.books.data.model.Book
import com.darekbx.books.data.model.ToRead
import com.darekbx.storage.books.BookDao
import com.darekbx.storage.books.BookDto
import com.darekbx.storage.books.ToReadDto
import com.darekbx.storage.legacy.OwnSpaceHelper
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val ownSpaceHelper: OwnSpaceHelper?,
    private val bookDao: BookDao
) {

    suspend fun update(book: Book) {
        book.id?.let {
            bookDao.updateBook(it, book.author, book.title, book.flags)
        }
    }

    suspend fun add(book: Book) {
        bookDao.add(BookDto(null, book.author, book.title, book.flags, book.year))
    }

    suspend fun add(toRead: ToRead) {
        bookDao.add(ToReadDto(null, toRead.author, toRead.title))
    }

    suspend fun deleteBook(bookId: Long) {
        bookDao.deleteBook(bookId)
    }

    suspend fun deleteToRead(toReadId: Long) {
        bookDao.deleteToRead(toReadId)
    }

    fun getBooks() = bookDao.getBooks()
        .map { books ->
            books.map { dto ->
                Book(dto.id!!, dto.author, dto.title, dto.flags, dto.year)
            }
        }

    suspend fun prepareLegacyBooks() {
        if (bookDao.countBooks() == 0) {
            fillBooksFromLegacyDatabase()
        }
    }

    suspend fun prepareLegacyToRead() {
        if (bookDao.countToRead() == 0) {
            fillToReadFromLegacyDatabase()
        }
    }

    fun getToRead() = bookDao.getToRead()
        .map { list ->
            list.map { dto -> ToRead(dto.id!!, dto.author, dto.title) }
        }

    private suspend fun fillBooksFromLegacyDatabase() {
        val listOfLegacyEntries = ownSpaceHelper?.getBooks() ?: emptyList()
        bookDao.addAllBooks(listOfLegacyEntries.map {
            BookDto(null, it.author, it.title, it.flags, it.year)
        })
    }

    private suspend fun fillToReadFromLegacyDatabase() {
        val listOfLegacyEntries = ownSpaceHelper?.getToRead() ?: emptyList()
        bookDao.addAllToRead(listOfLegacyEntries.map {
            ToReadDto(null, it.author, it.title)
        })
    }
}
