package com.darekbx.storage.words

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert
    suspend fun addWord(wordDto: WordDto)

    @Query("SELECT * FROM word_item")
    fun fetchWords(): Flow<List<WordDto>>

    @Query("DELETE FROM word_item WHERE id = :itemId")
    suspend fun deleteWord(itemId: Long)
}
