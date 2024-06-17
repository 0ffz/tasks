package me.dvyy.tasks.model.sync

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.ListKey

@Serializable
class ProjectNetworkModel(
    val key: ListKey.Project,
    val title: String?,
)
