package me.dvyy.tasks.model

import kotlinx.serialization.Serializable

@Serializable
class ProjectNetworkModel(
    val key: ListKey.Project,
    val name: String,
)
