package me.dvyy.tasks.app.ui

import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskModel

@Stable
data class Task(
    val name: String,
    val completed: Boolean,
    val key: ListKey,
    val highlight: Highlight,
) {
    fun toModel(uuid: Uuid): TaskModel = TaskModel(
        uuid = uuid,
        name = name,
        completed = completed,
        highlight = highlight,
//        syncStatus = SyncStatus.LOCAL_MODIFIED,
    )

    companion object {
        fun fromModel(model: TaskModel, key: ListKey) = Task(
            name = model.name,
            completed = model.completed,
            key = key,
            highlight = model.highlight,
        )
    }
}
