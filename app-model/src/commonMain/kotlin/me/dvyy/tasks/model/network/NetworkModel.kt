package me.dvyy.tasks.model.network

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType

@Serializable
sealed interface NetworkModel {
    val entityType: EntityType
}
