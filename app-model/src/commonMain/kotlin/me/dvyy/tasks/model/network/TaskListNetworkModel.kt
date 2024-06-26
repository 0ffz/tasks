package me.dvyy.tasks.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("task_list")
data class TaskListNetworkModel(
    val displayName: String? = null,
    val isProject: Boolean = true,
    val rank: Long,
) : NetworkModel
