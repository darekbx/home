package com.darekbx.storage.lifetimememo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lifetimememo.data.dto.ContainerDto
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Insert
    suspend fun addMemos(memos: List<MemoDto>)

    @Insert
    suspend fun addContainers(containers: List<ContainerDto>)

    @Insert
    suspend fun addCategories(categories: List<CategoryDto>)

    @Insert
    suspend fun addLocations(locations: List<LocationDto>)

    @Query("SELECT * FROM memo")
    fun allMemos(): Flow<List<MemoDto>>

    @Query("SELECT * FROM container")
    fun allContainers(): Flow<List<ContainerDto>>

    @Query("SELECT * FROM category")
    fun allCategories(): Flow<List<CategoryDto>>

    @Query("SELECT * FROM location")
    fun allLocations(): Flow<List<LocationDto>>
}
