package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

//@Serializable
//data class TaskChangeList(
//    val deletedProjects: List<ListKey.Project> = listOf(),
//    val createdProjects: List<ProjectNetworkModel> = listOf(),
//    val deletedTasks: List<Message<@Contextual Uuid>> = listOf(),
//    val updatedTasks: Map<ListKey, List<TaskModel>> = mapOf(),
//)

@Serializable
data class Changelist<T>(
    val lastSynced: Instant?,
    val changes: List<Message<T>>
) {
    fun compactedMap(): Map<Uuid, Message<T>> =
        changes.groupBy { it.uuid }
            .mapValues { it.value.reduce { acc, message -> acc.merge(message) } }
}
