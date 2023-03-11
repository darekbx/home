package com.darekbx.storage.notes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {

    @Insert
    suspend fun addAll(noteDto: List<NoteDto>)

    @Query("SELECT COUNT(id) FROM note")
    suspend fun countNotes(): Int

    @Query("SELECT * FROM note")
    suspend fun getNotes(): List<NoteDto>

    @Query("UPDATE note SET contents = :contents WHERE id = :id")
    suspend fun updateNote(id: Long, contents: String)
}
