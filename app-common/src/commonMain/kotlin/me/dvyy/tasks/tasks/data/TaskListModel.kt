package me.dvyy.tasks.tasks.data

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.TaskModel

@Serializable
data class TaskListModel(
    val properties: TaskListProperties = TaskListProperties(),
    val tasks: List<TaskModel> = emptyList(),
)
