package me.dvyy.tasks.tasks.ui

import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId

data class TaskReorderInteractions(
    val onDragEnterItem: (targetTask: TaskId, dragged: TaskId) -> Unit = { _, _ -> },
    val onDragEnterColumn: (targetList: VaultPath, dragged: TaskId) -> Unit = { _, _ -> },
)
