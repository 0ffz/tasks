package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskModel

@Stable
data class TaskUiState(
    val name: String,
    val completed: Boolean,
    val highlight: Highlight,
) {
    fun toModel(id: TaskId): TaskModel = TaskModel(
        id = id,
        name = name,
        completed = completed,
        highlight = highlight,
    )

    companion object {
        fun fromModel(model: TaskModel) = TaskUiState(
            name = model.name,
            completed = model.completed,
            highlight = model.highlight,
        )
    }
}
