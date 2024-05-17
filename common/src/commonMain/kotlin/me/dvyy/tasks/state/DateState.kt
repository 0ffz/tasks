package me.dvyy.tasks.state

import androidx.compose.runtime.Immutable

@Immutable
data class TaskListState(
    val name: String,
    val tasks: List<TaskState>,
)
