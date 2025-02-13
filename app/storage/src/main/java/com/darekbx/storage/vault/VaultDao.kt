package com.darekbx.storage.vault

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {

    @Insert
    suspend fun addAll(items: List<VaultDto>)

    @Insert
    suspend fun add(item: VaultDto)

    @Query("UPDATE vault SET `key` = :key, account = :account, password = :password WHERE id = :id")
    suspend fun update(id: Long, key: String, account: String, password: String)

    @Query("DELETE FROM vault WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM vault")
    suspend fun deleteAll()

    @Query("SELECT COUNT(id) FROM vault")
    suspend fun count(): Int

    @Query("SELECT * FROM vault")
    fun getItems(): Flow<List<VaultDto>>

    @Query("SELECT * FROM vault WHERE id = :id")
    suspend fun getItem(id: Long): VaultDto
}