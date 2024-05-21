package me.dvyy.tasks.tasks.ui

import com.benasher44.uuid.Uuid
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import me.dvyy.tasks.tasks.ui.elements.list.TaskListKey

data class TaskReorderInteractions(
    val draggedState: ReorderState<Uuid>,
    val onDragEnterItem: (targetTask: Uuid, dragged: DraggedItemState<Uuid>) -> Unit = { _, _ -> },
    val onDragEnterColumn: (targetList: TaskListKey, dragged: DraggedItemState<Uuid>) -> Unit = { _, _ -> },
)
