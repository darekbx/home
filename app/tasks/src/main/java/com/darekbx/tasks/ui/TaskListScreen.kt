package com.darekbx.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.tasks.data.model.Task

@Composable
fun TaskListScreen(
    tasksViewModel: TasksViewModel = hiltViewModel(),
    openTask: (Long) -> Unit = { }
) {
    val tasks by tasksViewModel.listTasks().collectAsState(initial = emptyList())

    LazyColumn(Modifier.fillMaxWidth()) {
        items(tasks) { task ->
            ItemView(Modifier.clickable { openTask(task.id) }, task)
        }
    }
}

@Composable
private fun ItemView(modifier: Modifier = Modifier, task: Task) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = task.name, style = MaterialTheme.typography.titleMedium)
        Text(text = task.date, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
    }
}

@Preview
@Composable
private fun ItemView() {
    HomeTheme {
        ItemView(task = Task(1L, "Name", "", "Date"))
    }
}
