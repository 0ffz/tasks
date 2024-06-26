package me.dvyy.tasks.model.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Changelist(
    val lastSynced: Instant,
    val upTo: Instant,
    val messages: List<NetworkMessage>,
)
