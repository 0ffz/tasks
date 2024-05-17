package me.dvyy.tasks.stateholder

import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import me.dvyy.tasks.state.TaskState

data class TaskReorder(
    val state: ReorderState<TaskState>,
    val onDragEnterItem: (target: TaskState, state: DraggedItemState<TaskState>) -> Unit,
)
