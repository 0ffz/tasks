package me.dvyy.tasks.ui.elements.task

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
import me.dvyy.tasks.platforms.PlatformSpecifics
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.stateholder.TaskInteractions
import me.dvyy.tasks.stateholder.TaskReorderInteractions
import me.dvyy.tasks.ui.elements.week.TaskWithIDState


@Composable
fun ReorderableTask(
    task: TaskWithIDState,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskInteractions,
    selected: Boolean,
) {
    ReorderableItem(
        state = reorderInteractions.draggedState,
        key = task,
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
                    TaskHighlight(task.state.name, task.state.highlight)
                    TaskTextPadding {
                        Text(task.state.name, Modifier.height(ui.taskHeight))
                    }
                }
            }
        },
        onDragEnter = { reorderInteractions.onDragEnterItem(task.uuid, it) },
    ) {
        Task(task.state, selected, interactions)
    }
    HorizontalDivider()
}
