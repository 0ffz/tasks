package me.dvyy.tasks.tasks.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle

@Serializable
data class TaskListModel(
    val title: ListTitle.Project? = null,
    val tasks: List<TaskModel> = emptyList(),
    val lastSynced: Instant? = null,
)
