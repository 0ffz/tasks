package me.dvyy.tasks.model.network

import com.benasher44.uuid.Uuid
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType

@Serializable
@SerialName("rank")
data class RankNetworkModel(
    val uuid: @Contextual Uuid,
    val parent: @Contextual Uuid,
    val rank: String,
) : NetworkModel {
    override val entityType: EntityType get() = EntityType.RANK
}
