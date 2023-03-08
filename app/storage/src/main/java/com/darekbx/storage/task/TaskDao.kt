package com.darekbx.storage.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    suspend fun addAll(taskDtos: List<TaskDto>)

    @Query("SELECT COUNT(id) FROM task")
    suspend fun countTasks(): Int

    @Query("SELECT * FROM task")
    suspend fun getTasks(): List<TaskDto>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: Long): TaskDto
}
