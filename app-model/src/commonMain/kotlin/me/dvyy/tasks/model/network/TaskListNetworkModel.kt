package me.dvyy.tasks.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType

@Serializable
@SerialName("task_list")
data class TaskListNetworkModel(
    val title: String? = null,
    val isProject: Boolean = true,
    val rank: Long,
) : NetworkModel {
    override val entityType: EntityType get() = EntityType.LIST
}
