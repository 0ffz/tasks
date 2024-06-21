package me.dvyy.tasks.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TaskListProperties(
    val displayName: String? = null,
    val date: LocalDate? = null,
    val lastSynced: Instant? = null,
)
