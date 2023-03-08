package com.darekbx.tasks.data

import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.task.TaskDao
import com.darekbx.storage.task.TaskDto
import com.darekbx.tasks.data.model.Task
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val ownSpaceHelper: OwnSpaceHelper?
) {
    suspend fun listTasks(): List<Task> {
        val tasksCount = taskDao.countTasks()
        if (tasksCount == 0) {
            val listOfLegacyTasks = ownSpaceHelper?.getTasks() ?: emptyList()
            taskDao.addAll(listOfLegacyTasks.map { TaskDto(null, it.name, it.content, it.date) })
        }

        return taskDao.getTasks().map {
            with(it) {
                Task(id!!, name, content, date)
            }
        }
    }

    suspend fun getTask(id: Long): Task {
        val taskDto = taskDao.getTask(id)
        return with(taskDto) {
            Task(id!!, name, content, date)
        }
    }
}
