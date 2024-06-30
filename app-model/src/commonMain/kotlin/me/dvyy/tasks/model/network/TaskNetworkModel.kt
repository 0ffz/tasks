package me.dvyy.tasks.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId

@Serializable
@SerialName("task")
data class TaskNetworkModel(
    val listId: ListId,
    val text: String? = null,
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val rank: Long,
) : NetworkModel {
    override val entityType: EntityType get() = EntityType.TASK
}
