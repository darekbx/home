package com.darekbx.storage.hejto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HejtoDao {

    @Insert
    suspend fun add(favouriteTagDto: FavouriteTagDto)

    @Query("DELETE FROM favourite_tag WHERE name = :name")
    suspend fun remove(name: String)

    @Query("UPDATE favourite_tag SET entries_count = :entriesCount WHERE name = :name")
    suspend fun update(name: String, entriesCount: Int)

    @Query("SELECT * FROM favourite_tag")
    suspend fun listAll(): List<FavouriteTagDto>

    @Query("SELECT COUNT(*) FROM favourite_tag WHERE name = :name")
    suspend fun contains(name: String): Int
}
