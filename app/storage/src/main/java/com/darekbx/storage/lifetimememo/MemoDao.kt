package com.darekbx.storage.lifetimememo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.darekbx.lifetimememo.data.dto.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Query("""
        SELECT 
            category.uid AS uid, 
            category.name AS name,
			(SELECT COUNT(memo.uid) FROM memo WHERE memo.category_uid = category.uid) AS count 
        FROM category
        """)
    fun categories(): Flow<List<ExtendedCategoryDto>>

    @Query("SELECT COUNT(uid) FROM memo")
    fun countMemos(): Flow<Int>

    @Query("SELECT COUNT(uid) FROM container")
    fun countContainers(): Flow<Int>

    @Query("SELECT * FROM memo WHERE container_uid = :containerId")
    fun memos(containerId: String?): Flow<List<MemoDto>>

    @Query("SELECT * FROM memo WHERE container_uid IS NULL")
    fun memos(): Flow<List<MemoDto>>

    @Query("SELECT * FROM container WHERE parent_uid IS NULL")
    fun containers(): Flow<List<ContainerDto>>

    @Query("SELECT * FROM container WHERE uid = :parentId")
    fun container(parentId: String): Flow<ContainerDto>

    @Query("SELECT * FROM location WHERE memo_id = :memoId")
    fun location(memoId: String): Flow<LocationDto?>

    @Query("SELECT * FROM container WHERE uid = :parentId")
    suspend fun containerSync(parentId: String): ContainerDto?

    @Query("SELECT * FROM container WHERE parent_uid = :containerId")
    fun containers(containerId: String): Flow<List<ContainerDto>>

    @Query("SELECT COUNT(uid) FROM container WHERE parent_uid = :containerId")
    suspend fun countContainers(containerId: String): Int

    @Query("SELECT COUNT(uid) FROM memo WHERE container_uid = :containerId")
    suspend fun countMemos(containerId: String): Int

    @Query("SELECT COUNT(uid) FROM location WHERE memo_id = :memoId")
    suspend fun countLocations(memoId: String): Int

    @Query("SELECT * FROM category WHERE uid = :categoryId")
    suspend fun getCategory(categoryId: String): CategoryDto

    @Query("SELECT * FROM memo WHERE uid = :id")
    suspend fun getMemo(id: String): MemoDto

    @Insert
    suspend fun add(categoryDto: CategoryDto): Long

    @Query("DELETE FROM category WHERE uid = :uid")
    suspend fun deleteCategory(uid: String)

    @Query("DELETE FROM container WHERE uid = :uid")
    suspend fun deleteContainer(uid: String)

    @Query("DELETE FROM memo WHERE uid = :uid")
    suspend fun deleteMemo(uid: String)

    @Query("UPDATE location SET latitude = :lat, longitude = :lng WHERE memo_id = :memoId")
    fun updateLocation(memoId: String, lat: Double, lng: Double)

    @Insert
    fun add(locationDto: LocationDto): Long

    @Insert
    fun add(entryDto: MemoDto): Long

    @Update
    fun update(entryDto: MemoDto)

    @Insert
    fun add(containerDto: ContainerDto): Long
}