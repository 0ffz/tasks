package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskChangeList(
    val deletedProjects: List<ListKey.Project> = listOf(),
    val createdProjects: List<ProjectNetworkModel> = listOf(),
    val deletedTasks: List<Timestamped<@Contextual Uuid>> = listOf(),
    val updatedTasks: Map<ListKey, List<TaskModel>> = mapOf(),
)

@Serializable
data class Timestamped<T>(val data: T, val timestamp: Instant)
