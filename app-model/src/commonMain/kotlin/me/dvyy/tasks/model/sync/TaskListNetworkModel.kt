package me.dvyy.tasks.model.sync

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties

@Serializable
class TaskListNetworkModel(
    val key: ListId,
    val properties: TaskListProperties,
)
