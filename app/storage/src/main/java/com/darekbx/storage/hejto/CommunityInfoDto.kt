package com.darekbx.storage.hejto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_info")
class CommunityInfoDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "slug") val slug: String,
    @ColumnInfo(name = "posts_count") val postsCount: Int = 0
)
