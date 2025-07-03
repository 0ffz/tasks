package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Task(
    val text: String,
    val done: Boolean = false,
    val parent: Uuid,
    val rank: String? = null,
)
