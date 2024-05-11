package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.createEmptyTask
import me.dvyy.tasks.logic.Tasks.delete
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
    fun nextTaskOrNew() {
        if (date.tasks.value.lastOrNull() != task) {
            focusManager.moveFocus(FocusDirection.Down)
        } else if (task.name.value.isNotEmpty()) {
            date.createEmptyTask(app, focus = true)
        }
    }
    Task(
        task,
        interactions = TaskInteractions(
            onNameChange = {
                app.queueSaveDay(date)
                task.name.value = it
            },
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
    HorizontalDivider()
}
