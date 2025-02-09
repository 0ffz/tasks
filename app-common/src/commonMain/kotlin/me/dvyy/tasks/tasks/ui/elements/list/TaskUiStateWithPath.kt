package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Immutable
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Immutable
data class TaskUiStateWithPath(
    val state: TaskUiState,
    val path: VaultPath,
)
