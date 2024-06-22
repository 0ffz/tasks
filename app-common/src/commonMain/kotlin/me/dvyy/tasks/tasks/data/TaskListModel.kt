package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.TaskListProperties

data class TaskListModel(
    val properties: TaskListProperties = TaskListProperties(),
    val tasks: List<Task> = emptyList(),
)
