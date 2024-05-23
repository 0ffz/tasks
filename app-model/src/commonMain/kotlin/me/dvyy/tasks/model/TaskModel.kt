package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskModel(
    val uuid: @Contextual Uuid,
    val name: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val lastModified: Instant,
//    val syncStatus: SyncStatus = SyncStatus.SYNCED,
)

