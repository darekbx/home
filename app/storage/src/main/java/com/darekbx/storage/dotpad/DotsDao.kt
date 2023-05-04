package com.darekbx.storage.dotpad
import androidx.lifecycle.LiveData
import androidx.room.*
import com.darekbx.dotpad.repository.local.entities.DotDto
import com.darekbx.dotpad.repository.local.entities.StatisticsEntity

@Dao
interface DotsDao {

    @Query("SELECT COUNT(id) FROM dots")
    fun countDots(): Int

    @Insert
    fun addAll(dotDtos: List<DotDto>)

    @Insert
    fun add(dotDto: DotDto): Long

    @Query("SELECT * FROM dots")
    fun fetchAll(): LiveData<List<DotDto>>

    @Query("SELECT * FROM dots WHERE id = :dotId")
    fun fetchDot(dotId: Long): LiveData<DotDto>

    @Query("SELECT * FROM dots WHERE is_archived = 0 ORDER BY created_date ASC")
    fun fetchActive(): LiveData<List<DotDto>>

    @Query("SELECT * FROM dots WHERE is_archived = 1 ORDER BY created_date DESC LIMIT :limit OFFSET :offset")
    fun fetchArchive(limit: Int, offset: Int): LiveData<List<DotDto>>

    @Query("SELECT * FROM dots WHERE text LIKE :query ORDER BY created_date")
    fun search(query: String): LiveData<List<DotDto>>

    @Query("UPDATE dots SET position_x = :x, position_y = :y WHERE id = :dotId")
    fun updateDotPosition(dotId: Long?, x: Int, y: Int)

    @Query("SELECT COUNT(id) FROM dots")
    fun countStatistics(): LiveData<Int>

    @Query("SELECT COUNT(size) AS occurrences, size AS value FROM dots GROUP BY size")
    fun sizeStatistics(): LiveData<List<StatisticsEntity>>

    @Query("SELECT COUNT(color) AS occurrences, color AS value FROM dots GROUP BY color")
    fun colorStatistics(): LiveData<List<StatisticsEntity>>

    @Update
    fun update(dotDto: DotDto)

    @Query("UPDATE dots SET color = :newColor WHERE color = :oldColor")
    fun updateColor(newColor: Int, oldColor: Int) : Int

    @Query("DELETE FROM dots WHERE id = :dotId")
    fun deleteDot(dotId: Long)

    @Query("DELETE FROM dots")
    fun deleteAll()
}
