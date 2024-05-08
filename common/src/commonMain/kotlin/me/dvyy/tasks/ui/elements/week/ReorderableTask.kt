package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.focusable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.createTask
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
    println("Recomposing ${task.name} $date!")
    val dragAfterLongPress by app.isSmallScreen
        .map { it || PlatformSpecifics.preferLongPressDrag }
        .collectAsState(false)

    ReorderableItem(
        state = reorderState,
        key = task,
        data = task,
        dragAfterLongPress = dragAfterLongPress,
        zIndex = 1f,
        onDragEnter = { onDragEnterItem(task, it) }
    ) {
        fun nextTaskOrNew() {
            if (date.tasks.value.lastOrNull() != task) {
                focusManager.moveFocus(FocusDirection.Down)
            } else if (task.name.value.isNotEmpty()) {
                app.createTask(me.dvyy.tasks.logic.Task("", date.date), focus = true)
            }
        }
        Task(
            task,
            onNameChange = {
                println("Updating to $it")
                task.name.value = it
            },
            onKeyEvent = { event ->
                if (event.type != KeyEventType.KeyDown) return@Task false
                when {
                    event.isCtrlPressed && event.key == Key.E -> {
                        task.highlight.update {
                            Highlights.entries[(it.ordinal + 1) % Highlights.entries.size]
                        }
                        true
                    }

                    event.key == Key.Enter -> {
                        nextTaskOrNew()
                        true
                    }

                    else -> false
                }

            },
            onNext = {
                nextTaskOrNew()
            }
        )
    }
    HorizontalDivider()
}