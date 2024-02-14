package com.darekbx.storage.words

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert
    suspend fun addWord(wordDto: WordDto)

    @Query("SELECT * FROM word_item ORDER BY is_archived ASC")
    fun fetchWords(): Flow<List<WordDto>>

    @Query("DELETE FROM word_item WHERE id = :itemId")
    suspend fun deleteWord(itemId: Long)

    @Query("UPDATE word_item SET is_archived = :isArchived WHERE id = :itemId")
    suspend fun setArchived(itemId: Long, isArchived: Boolean)

    @Query("UPDATE word_item SET checked_count = :count WHERE id = :itemId")
    suspend fun setCount(itemId: Long, count: Int)
}
