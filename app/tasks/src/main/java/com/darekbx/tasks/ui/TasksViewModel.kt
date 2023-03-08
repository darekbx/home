package com.darekbx.tasks.ui

import androidx.lifecycle.ViewModel
import com.darekbx.tasks.data.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    fun listTasks() = flow {
        emit(tasksRepository.listTasks())
    }

    fun getTask(id: Long) = flow {
        emit(tasksRepository.getTask(id))
    }
}
