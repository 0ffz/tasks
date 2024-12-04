package me.dvyy.tasks.model.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class NetworkMessage(
        val data: NetworkModel,
        val entityId: @Contextual Uuid,
        val modified: Instant,
) {
    enum class Type {
        Delete,
        Update
    }
}
