package me.dvyy.tasks.ui.elements.task

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import me.dvyy.tasks.platforms.PlatformSpecifics
import me.dvyy.tasks.state.LocalTaskReorder
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.stateholder.TaskInteractions


@Composable
fun ReorderableTask(
    task: TaskState,
    interactions: TaskInteractions,
    selected: Boolean,
) {
//    QueueSaveWhenModified(date, task)


//    fun nextTaskOrNew() {
//        val tasks = date.tasks.value
//        if (tasks.lastOrNull() != task) {
//            tasks[tasks.indexOf(task) + 1].focusRequested.value = true
//        } else if (task.name.value.isNotEmpty()) {
//            date.createEmptyTask(app, focus = true)
//        }
//    }

    val reorder = LocalTaskReorder.current
    val onDragEnter = remember(task) {
        { it: DraggedItemState<TaskState> ->
            reorder.onDragEnterItem(task, it)
        }
    }
    ReorderableItem(
        state = reorder.state,
        key = task,
        data = task,
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
                TaskTextPadding {
                    Text(task.name, Modifier.height(ui.taskHeight))
                }
            }
        },
        onDragEnter = onDragEnter,
    ) {
        Task(task, selected, interactions)
    }
    HorizontalDivider()
}
