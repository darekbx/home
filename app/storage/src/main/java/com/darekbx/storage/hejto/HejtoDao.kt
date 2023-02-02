package com.darekbx.storage.hejto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HejtoDao {

    @Insert
    suspend fun addCommunityInfo(communityInfoDto: CommunityInfoDto)

    @Query("UPDATE community_info SET posts_count = :postsCount WHERE slug = :slug")
    suspend fun updateCommunityInfo(slug: String, postsCount: Int)

    @Query("SELECT posts_count FROM community_info WHERE slug = :slug LIMIT 1")
    suspend fun getCommunityInfoPosts(slug: String): Int?

    @Query("SELECT COUNT(id) FROM community_info WHERE slug = :slug")
    suspend fun containsComunityInfo(slug: String): Int

    @Query("DELETE FROM community_info")
    suspend fun clearCommunityInfo()

    @Insert
    suspend fun add(savedSlugDto: SavedSlugDto)

    @Query("DELETE FROM saved_slug WHERE slug = :slug")
    suspend fun removeSavedSlug(slug: String)

    @Query("SELECT * FROM saved_slug")
    suspend fun listSavedSlugs(): List<SavedSlugDto>

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
