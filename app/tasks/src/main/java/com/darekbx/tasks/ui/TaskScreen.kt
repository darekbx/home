package com.darekbx.tasks.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TaskScreen(tasksViewModel: TasksViewModel = hiltViewModel(), taskId: Int) {
    val task by tasksViewModel.getTask(taskId.toLong()).collectAsState(initial = null)
    task?.let {
        val scrollState = rememberScrollState(0)
        Text(modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
            .fillMaxSize(),
            text = it.content,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
