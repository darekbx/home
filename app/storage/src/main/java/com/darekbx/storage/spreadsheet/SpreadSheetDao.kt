package com.darekbx.storage.spreadsheet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darekbx.storage.spreadsheet.entities.SpreadSheetDto

@Dao
interface SpreadSheetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(spreadSheet: SpreadSheetDto)

    @Query("SELECT * FROM spread_sheet WHERE parent_uid IS NULL ORDER BY created_timestamp DESC")
    suspend fun fetchAll(): List<SpreadSheetDto>

    @Query("SELECT * FROM spread_sheet WHERE parent_uid = :parentUid ORDER BY created_timestamp DESC")
    suspend fun fetchAll(parentUid: String): List<SpreadSheetDto>

    @Query("SELECT * FROM spread_sheet WHERE uid = :uid")
    suspend fun fetch(uid: String): SpreadSheetDto?

    @Query("DELETE FROM spread_sheet WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("UPDATE spread_sheet SET name = :name, parent_name = :parentName WHERE uid = :uid")
    suspend fun updateName(uid: String, name: String, parentName: String)

    @Query("UPDATE spread_sheet SET updated_timestamp = :timestamp WHERE uid = :uid")
    suspend fun setUpdatedTimestamp(uid: String, timestamp: Long = System.currentTimeMillis())
}
