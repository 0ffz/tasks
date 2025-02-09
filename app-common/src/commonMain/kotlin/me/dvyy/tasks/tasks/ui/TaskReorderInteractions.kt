package me.dvyy.tasks.tasks.ui

import me.dvyy.tasks.database.VaultPath

data class TaskReorderInteractions(
    val onDragEnterItem: (targetTask: VaultPath, dragged: VaultPath) -> Unit = { _, _ -> },
    val onDragEnterColumn: (targetList: VaultPath, dragged: VaultPath) -> Unit = { _, _ -> },
)
