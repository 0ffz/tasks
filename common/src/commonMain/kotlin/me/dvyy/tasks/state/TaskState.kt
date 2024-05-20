package me.dvyy.tasks.state

import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.SyncStatus
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.ui.elements.week.TaskListKey

@Stable
data class TaskState(
    val name: String,
    val completed: Boolean,
    val key: TaskListKey,
    val highlight: Highlight,
) {
    fun toModel(uuid: Uuid): TaskModel = TaskModel(
        uuid = uuid,
        name = name,
        completed = completed,
        highlight = highlight,
        syncStatus = SyncStatus.LOCAL_MODIFIED,
    )

    companion object {
        fun fromModel(model: TaskModel, key: TaskListKey) = TaskState(
            name = model.name,
            completed = model.completed,
            key = key,
            highlight = model.highlight,
        )
    }
}
