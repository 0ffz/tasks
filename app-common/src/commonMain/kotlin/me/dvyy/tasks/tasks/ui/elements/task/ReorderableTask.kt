package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.PlatformSpecifics
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState


@Composable
fun ReorderableTask(
    key: TaskId,
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskInteractions,
    selected: Boolean,
    focusRequested: Boolean = false,
) {
    ReorderableItem(
        state = reorderInteractions.draggedState,
        key = key,
        enabled = true,
        data = key,
        dragAfterLongPress = PlatformSpecifics.preferLongPressDrag,
        zIndex = 1f,
        dropAnimationSpec = tween(0),
        draggableContent = {
            val ui = LocalUIState.current
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(horizontal = ui.horizontalTaskTextPadding)
            ) {
                TaskHighlight(task.text, task.highlight)
                TaskTextPadding {
                    Text(
                        task.text,
                        Modifier.height(ui.taskHeight),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = task.highlight.color.getBestTextColor()
                    )
                }
            }
        },
        onDragEnter = { reorderInteractions.onDragEnterItem(key, it) },
    ) {
        Task(task, setTask, selected, interactions, focusRequested = focusRequested)
    }
    HorizontalDivider()
}
