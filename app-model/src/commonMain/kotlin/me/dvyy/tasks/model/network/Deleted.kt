package me.dvyy.tasks.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType

@Serializable
@SerialName("deleted")
class Deleted(
    val entityType: EntityType,
) : NetworkModel
