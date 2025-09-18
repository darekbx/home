package com.darekbx.storage.notebookcheckreader

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RssFavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: RssFavouriteItemDto)

    @Query("DELETE FROM rss_favourite_items WHERE itemId = :rssItemId")
    suspend fun delete(rssItemId: String)

    @Query("SELECT * FROM rss_favourite_items WHERE itemId = :rssItemId")
    suspend fun getById(rssItemId: String): RssFavouriteItemDto?

    @Query("SELECT * FROM rss_favourite_items")
    suspend fun fetch(): List<RssFavouriteItemDto>

    @Query("SELECT COUNT(*) FROM rss_favourite_items")
    fun fetchCount(): Flow<Int>
}
