package me.dvyy.tasks.tasks.ui.state

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Stable
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.ImeAction
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.components.TaskModel

@Stable
data class TaskUiState(
    val text: String,
    val completed: Boolean,
    val highlight: Highlight,
) {
    fun toModel() = TaskModel(text, completed, highlight)
    companion object {
        fun fromModel(model: TaskModel) = TaskUiState(
            text = model.text ?: "",
            completed = model.done,
            highlight = model.highlight ?: Highlight.Unmarked, //TODO swap to tag system
        )
    }
}

@Stable
data class TaskState(
    val uiState: TaskUiState,
    val selected: Boolean,
    val date: LocalDate? = null,
    val setTask: (TaskUiState) -> Unit,
    val mutate: TaskMutations,
) {
    val keyboardActions: KeyboardActions get() = KeyboardActions()
    val keyboardOptions: KeyboardOptions get() = KeyboardOptions(imeAction = ImeAction.Next)

    inline fun updateUi(update: (TaskUiState) -> TaskUiState) {
        setTask(update(uiState))
    }
}

interface TaskMutations {
    fun moveTo(date: LocalDate) {}
    fun onDelete() {}
    fun onKeyEvent(event: KeyEvent): Boolean = false
    fun onSelect() {}
    fun dropTaskOnThis(other: TaskId)
}

class ReorderableItemState {
    fun onListChanged(date: LocalDate) {}
}