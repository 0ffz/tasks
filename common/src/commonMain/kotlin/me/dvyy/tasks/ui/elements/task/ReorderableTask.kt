package me.dvyy.tasks.ui.elements.task

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.createEmptyTask
import me.dvyy.tasks.logic.Tasks.delete
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.platforms.PlatformSpecifics
import me.dvyy.tasks.state.AppConstants
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState


data class TaskReorder(
    val state: ReorderState<TaskState>,
    val onDragEnterItem: (target: TaskState, state: DraggedItemState<TaskState>) -> Unit,
)

val LocalTaskReorder = compositionLocalOf<TaskReorder> { error("No TaskReorder provided") }

@Composable
fun ReorderableTask(
    date: DateState,
    task: TaskState,
) {
    val app = LocalAppState
    val focusManager = LocalFocusManager.current

    QueueSaveWhenModified(date, task)


    fun nextTaskOrNew() {
        val tasks = date.tasks.value
        if (tasks.lastOrNull() != task) {
            tasks[tasks.indexOf(task) + 1].focusRequested.value = true
        } else if (task.name.value.isNotEmpty()) {
            date.createEmptyTask(app, focus = true)
        }
    }

    val reorder = LocalTaskReorder.current
    val onDragEnter = remember(task) {
        { it: DraggedItemState<TaskState> ->
            app.selectedTask.value = null
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
                TaskTextPadding {
                    val name by task.name.collectAsState()
                    Text(name, Modifier.height(AppConstants.taskHeight))
                }
            }
        },
        onDragEnter = onDragEnter,
    ) {
        println("Recomposed task ${task}")
        Task(
            task,
            interactions = TaskInteractions(
                onNameChange = { task.name.value = it },
                onKeyEvent = { event ->
                    if (event.key == Key.Backspace) {
                        if (task.name.value.isEmpty()) {
                            focusManager.moveFocus(FocusDirection.Up)
                            task.delete(app)
                        }
                        return@TaskInteractions false
                    }
                    if (event.type != KeyEventType.KeyDown) return@TaskInteractions false
                    when {
                        event.isCtrlPressed && event.key == Key.E -> {
                            task.highlight.update {
                                Highlight.entries[(it.ordinal + 1) % Highlight.entries.size]
                            }
                            true
                        }

                        event.key == Key.Escape -> {
                            app.selectedTask.value = null
                            true
                        }

                        event.key == Key.Enter -> {
                            nextTaskOrNew()
                            true
                        }

                        else -> false
                    }

                },
                keyboardActions = KeyboardActions(onNext = { nextTaskOrNew() })
            ),
        )
    }
    HorizontalDivider()
}
