package me.dvyy.tasks.model.network

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMessage(
    val data: NetworkModel,
    val entityId: @Contextual Uuid,
    val modified: Instant,
) {
    enum class Type {
        Delete, Update
    }
}
