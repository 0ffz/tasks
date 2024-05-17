package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskModel(
    val uuid: @Contextual Uuid,
    val name: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
)

