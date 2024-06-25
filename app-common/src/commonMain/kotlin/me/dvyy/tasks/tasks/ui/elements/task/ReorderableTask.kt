package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.PlatformSpecifics
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskWithIDState
import me.dvyy.tasks.tasks.ui.state.TaskUiState


@Composable
fun ReorderableTask(
    task: TaskWithIDState,
    reorderInteractions: TaskReorderInteractions,
    getInteractions: (TaskUiState) -> TaskInteractions,
    selected: Boolean,
) {
    ReorderableItem(
        state = reorderInteractions.draggedState,
        key = task.uuid,
        data = task.uuid,
        dragAfterLongPress = PlatformSpecifics.preferLongPressDrag,
        zIndex = 1f,
        dropAnimationSpec = tween(0),
        draggableContent = {
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                val ui = LocalUIState.current
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.padding(horizontal = ui.taskTextPadding)
                ) {
                    TaskHighlight(task.state.text, task.state.highlight)
                    TaskTextPadding {
                        Text(task.state.text, Modifier.height(ui.taskHeight))
                    }
                }
            }
        },
        onDragEnter = { reorderInteractions.onDragEnterItem(task.uuid, it) },
    ) {
        Task(task.state, selected, getInteractions, key = task.uuid)
    }
    HorizontalDivider()
}
