package me.dvyy.tasks.model.network

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.EntityType
import kotlin.uuid.Uuid

@Serializable
@SerialName("rank")
data class RankNetworkModel(
    val uuid: @Contextual Uuid,
    val parent: @Contextual Uuid,
    val rank: String,
) : NetworkModel {
    init {
        require(rank.all { it in 'a'..'z' }) { "Rank must only contain characters a-z" }
    }

    override val entityType: EntityType get() = EntityType.RANK
}
