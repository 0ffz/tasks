package me.dvyy.tasks.tasks.ui

import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId

data class TaskReorderInteractions(
    val draggedState: ReorderState<TaskId>,
    val onDragEnterItem: (targetTask: TaskId, dragged: DraggedItemState<TaskId>) -> Unit = { _, _ -> },
    val onDragEnterColumn: (targetList: ListId, dragged: DraggedItemState<TaskId>) -> Unit = { _, _ -> },
)
