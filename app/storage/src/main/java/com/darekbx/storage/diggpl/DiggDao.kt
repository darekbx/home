package com.darekbx.storage.diggpl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiggDao {

    @Insert
    suspend fun addTag(savedTagDto: SavedTagDto)

    @Query("DELETE FROM saved_tag WHERE name = :name")
    suspend fun removeTag(name: String)

    @Query("UPDATE saved_tag SET last_date = :lastDate WHERE name = :name")
    suspend fun updateTag(name: String, lastDate: String)

    @Query("SELECT * FROM saved_tag")
    suspend fun listAllTags(): List<SavedTagDto>

    @Query("SELECT COUNT(*) FROM saved_tag WHERE name = :name")
    suspend fun containsTag(name: String): Int

    @Insert
    suspend fun add(savedLinkDto: SavedLinkDto)

    @Query("DELETE FROM saved_link WHERE link_id = :linkId")
    suspend fun removeSavedLink(linkId: Int)

    @Query("SELECT * FROM saved_link")
    suspend fun listSavedLinks(): List<SavedLinkDto>

    @Insert
    suspend fun add(savedEntryDto: SavedEntryDto)

    @Query("DELETE FROM saved_entry WHERE entry_id = :entryId")
    suspend fun removeSavedEntry(entryId: Int)

    @Query("SELECT * FROM saved_entry")
    suspend fun listSavedEntries(): List<SavedEntryDto>

    @Query("SELECT (SELECT COUNT(*) FROM saved_link) + (SELECT COUNT(*) FROM saved_entry)")
    suspend fun countSavedItems(): Int

}
