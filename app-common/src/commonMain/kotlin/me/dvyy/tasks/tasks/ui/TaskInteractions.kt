package me.dvyy.tasks.tasks.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.ImeAction
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.tasks.ui.state.TaskUiState

interface TaskInteractions {
    val keyboardActions: KeyboardActions get() = KeyboardActions()
    val keyboardOptions: KeyboardOptions get() = KeyboardOptions(imeAction = ImeAction.Next)

    fun onTaskChanged(newState: TaskUiState) {}
    fun onListChanged(date: LocalDate) {}
    fun onDelete() {}
    fun onKeyEvent(event: KeyEvent): Boolean = false
    fun onSelect() {}
}
