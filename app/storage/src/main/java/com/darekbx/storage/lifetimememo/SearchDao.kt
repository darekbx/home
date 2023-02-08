package com.darekbx.storage.lifetimememo

import androidx.room.Dao
import androidx.room.Query
import com.darekbx.lifetimememo.data.dto.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("""
SELECT 
    * 
FROM 
    memo 
WHERE 
    title LIKE '%' || :query || '%' 
OR 
    subtitle LIKE '%' || :query || '%'
OR 
    description LIKE '%' || :query || '%'
    """)
    fun searchMemos(query: String): Flow<List<MemoDto>>

    @Query("""
SELECT 
    * 
FROM 
    container 
WHERE 
    title LIKE '%' || :query || '%' 
OR 
    subtitle LIKE '%' || :query || '%'
    """)
    fun searchContainers(query: String): Flow<List<ContainerDto>>
}
