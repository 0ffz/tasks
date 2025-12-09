package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Immutable
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Immutable
data class TaskWithIDState(
    val state: TaskUiState,
    val uuid: TaskId,
)
