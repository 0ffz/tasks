package me.dvyy.tasks.stateholder

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.input.key.KeyEvent
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Highlight

@Immutable
data class TaskInteractions(
    val onTitleChanged: (String) -> Unit,
    val onCheckChanged: (Boolean) -> Unit,
    val onDateChanged: (LocalDate) -> Unit,
    val onHighlightChanged: (Highlight) -> Unit,
    val onDelete: () -> Unit,
    val onKeyEvent: (KeyEvent) -> Boolean = { false },
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val onSelect: () -> Unit,
)
