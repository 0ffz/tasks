package me.dvyy.tasks.tasks.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.ImeAction
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Immutable
data class TaskInteractions(
    val onTaskChanged: (TaskUiState) -> Unit = {},
//    val onTitleChanged: (String) -> Unit = {},
//    val onCheckChanged: (Boolean) -> Unit = {},
    val onListChanged: (LocalDate) -> Unit = {},
//    val onHighlightChanged: (Highlight) -> Unit = {},
    val onDelete: () -> Unit = {},
    val onKeyEvent: (KeyEvent, TaskUiState) -> Boolean = { _, _ -> false },
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    val onSelect: () -> Unit = {},
)
