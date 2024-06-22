package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.Highlight

@Stable
data class TaskUiState(
    val text: String,
    val completed: Boolean,
    val highlight: Highlight,
) {
    companion object {
        fun fromModel(model: Task) = TaskUiState(
            text = model.text ?: "",
            completed = model.completed,
            highlight = model.highlight,
        )
    }
}
