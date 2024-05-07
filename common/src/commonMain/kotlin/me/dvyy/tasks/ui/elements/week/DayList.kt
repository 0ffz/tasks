package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Dates.loadDate
import me.dvyy.tasks.logic.Task
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.platforms.PlatformSpecifics
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState

@Composable
fun DayList(
    date: LocalDate,
    isToday: Boolean,
    reorderState: ReorderState<TaskState>,
    onDragEnterColumn: (dateState: DateState, state: DraggedItemState<TaskState>) -> Unit,
    onDragEnterItem: (target: TaskState, state: DraggedItemState<TaskState>) -> Unit,
    fullHeight: Boolean,
    modifier: Modifier = Modifier,
) {
    val app = LocalAppState
    val state = remember(date) { app.loadDate(date) }

    Column(modifier) {
        DayTitle(state.date, isToday)

        Column(
            modifier = Modifier.padding(vertical = 8.dp).heightIn(max = 1000.dp)
                .dropTarget(
                    key = state.date,
                    state = reorderState.dndState,
                    dropAnimationEnabled = false,
                    onDragEnter = { onDragEnterColumn(state, it) },
                )
        ) {
            val tasks by state.tasks.collectAsState()
            LazyColumn {
                items(tasks, key = { it.uuid }) { task ->
                    ReorderableTask(state, task, onDragEnterItem, reorderState)
                }
            }
            val emptySpace = remember(fullHeight) {
                if (fullHeight) Modifier.fillMaxHeight() else Modifier.height(40.dp)
            }
            Box(
                modifier = emptySpace.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (tasks.lastOrNull()?.name?.value?.isEmpty() != true)
                        app.createTask(Task("", state.date), focus = true)
                })
        }
    }
}

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
    ReorderableItem(
        state = reorderState,
        key = task,
        data = task,
        dragAfterLongPress = PlatformSpecifics.preferLongPressDrag,
        zIndex = 1f,
        onDragEnter = {
            onDragEnterItem(task, it)
        },
    ) {
        fun nextTaskOrNew() {
            if (date.tasks.value.lastOrNull() != task) {
                focusManager.moveFocus(FocusDirection.Down)
            } else if (task.name.value.isNotEmpty()) {
                app.createTask(Task("", date.date), focus = true)
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

@Composable
fun NonlazyGrid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable() (Int) -> Unit
) {
    Column(modifier = modifier) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}
