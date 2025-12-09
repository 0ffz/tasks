package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.components.Task

@Stable
data class TaskUiState(
    val text: String,
    val completed: Boolean,
    val highlight: Highlight,
) {
    companion object {
        fun fromModel(model: Task) = TaskUiState(
            text = model.text ?: "",
            completed = model.done,
            highlight = Highlight.Unmarked, //TODO swap to tag system
        )
    }
}
