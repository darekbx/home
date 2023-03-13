package com.darekbx.storage.books

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun add(bookDto: BookDto)

    @Insert
    suspend fun addAllBooks(bookDto: List<BookDto>)

    @Insert
    suspend fun add(toReadDto: ToReadDto)

    @Insert
    suspend fun addAllToRead(toReadDto: List<ToReadDto>)

    @Query("SELECT COUNT(id) FROM book")
    suspend fun countBooks(): Int

    @Query("SELECT COUNT(id) FROM book_to_read")
    suspend fun countToRead(): Int

    @Query("UPDATE book SET author = :author, title = :title, flags = :flags WHERE id = :id")
    suspend fun updateBook(id: Long, author: String, title: String, flags: String)

    @Query("DELETE FROM book WHERE id = :id")
    suspend fun deleteBook(id: Long)

    @Query("DELETE FROM book_to_read WHERE id = :id")
    suspend fun deleteToRead(id: Long)

    @Query("SELECT * FROM book ORDER BY `id` DESC")
    fun getBooks(): Flow<List<BookDto>>

    @Query("SELECT * FROM book_to_read")
    fun getToRead(): Flow<List<ToReadDto>>
}
