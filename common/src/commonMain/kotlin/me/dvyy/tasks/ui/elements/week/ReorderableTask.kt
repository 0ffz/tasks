package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.createEmptyTask
import me.dvyy.tasks.platforms.PlatformSpecifics
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState

@Composable
fun ReorderableTask(
    date: DateState,
    task: TaskState,
    onDragEnterItem: (target: TaskState, state: DraggedItemState<TaskState>) -> Unit,
    reorderState: ReorderState<TaskState>,
) {
    val app = LocalAppState
    val focusManager = LocalFocusManager.current
    val active by task.isActive(app)
    ReorderableItem(
        enabled = !active,
        state = reorderState,
        key = task,
        data = task,
        dragAfterLongPress = PlatformSpecifics.preferLongPressDrag,
        zIndex = 1f,
        dropAnimationSpec = tween(0),
        onDragEnter = { onDragEnterItem(task, it) },
    ) {
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
                    println("Updating to $it")
                    app.queueSaveDay(date)
                    task.name.value = it
                },
                onKeyEvent = { event ->
                    if (event.type != KeyEventType.KeyDown) return@TaskInteractions false
                    when {
                        event.isCtrlPressed && event.key == Key.E -> {
                            task.highlight.update {
                                Highlights.entries[(it.ordinal + 1) % Highlights.entries.size]
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
