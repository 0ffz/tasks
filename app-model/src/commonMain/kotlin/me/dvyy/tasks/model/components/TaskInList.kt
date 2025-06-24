package me.dvyy.tasks.model.components

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class TaskInList(
    val list: Uuid,
    val task: Uuid,
)