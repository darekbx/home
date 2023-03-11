package com.darekbx.notepad.data

import com.darekbx.notepad.data.model.Note
import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.notes.NoteDto
import com.darekbx.storage.notes.NotesDao
import javax.inject.Inject

class NotepadRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val ownSpaceHelper: OwnSpaceHelper?
) {

    suspend fun getNotes(): List<Note> {
        val count = notesDao.countNotes()
        if (count == 0) {
            val listOfLegacyNotes = ownSpaceHelper?.getNotes() ?: emptyList()
            notesDao.addAll(listOfLegacyNotes.map { NoteDto(null, it) })
        }
        return notesDao.getNotes().map { Note(it.id!!, it.contents) }
    }

    suspend fun updateNote(id: Long, contents: String) {
        notesDao.updateNote(id, contents)
    }
}
