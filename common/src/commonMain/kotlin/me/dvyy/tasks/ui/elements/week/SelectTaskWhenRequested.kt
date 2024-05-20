package me.dvyy.tasks.ui.elements.week

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import me.dvyy.tasks.stateholder.TasksViewModel

@Composable
fun SelectTaskWhenRequested(tasksStateHolder: TasksViewModel = viewModel()) {
    // Always play the selection animation when a task is created
    // by emitting a state update after the UI composition. TODO is there a better way?
    val requestedSelect by tasksStateHolder.requestedSelectTask.collectAsState()
    LaunchedEffect(requestedSelect) {
        tasksStateHolder.selectedTask.emit(requestedSelect)
    }
}