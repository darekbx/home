package com.darekbx.storage.emailbot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SpamDao {

    @Insert
    suspend fun insert(spamDto: SpamDto)

    @Query("SELECT * FROM spam_item")
    suspend fun getAll(): List<SpamDto>


    @Query("DELETE FROM spam_item WHERE id = :id")
    suspend fun delete(id: String)
}
