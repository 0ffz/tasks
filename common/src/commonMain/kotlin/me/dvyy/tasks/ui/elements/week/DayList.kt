package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
) {
    val lazyListState = rememberLazyListState()
    val app = LocalAppState
    val state = remember(date) { app.loadDate(date) }

    Column(
        Modifier.fillMaxSize()
    ) {
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
            val tasks = state.tasks
            LazyColumn {
                items(tasks, key = { it.uuid }) { task ->
//                    TestTask(task, StableWrap(date))
                    val date = remember { StableWrap(date) }
                    ReorderableTask(date, task, onDragEnterItem, reorderState)
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

//@Composable
//fun TestTask(task: TaskState, date: StableWrap<LocalDate>) {
//    val name by task.name.collectAsState()
//    println("Recomposing ${task.name} $date!")
//    Text("Hey $name")
//}

@Stable
data class StableWrap<T>(val data: T)

@Composable
fun ReorderableTask(
    date: StableWrap<LocalDate>,
    task: TaskState,
    onDragEnterItem: (target: TaskState, state: DraggedItemState<TaskState>) -> Unit,
    reorderState: ReorderState<TaskState>,
) {
    val date = date.data
    val app = LocalAppState
    val focusManager = LocalFocusManager.current
    println("Recomposing ${task.name} $date!")
    ReorderableItem(
        state = reorderState,
        key = task,
        data = task,
        zIndex = 1f,
        onDragEnter = {
            onDragEnterItem(task, it)
        },
    ) {
        fun nextTaskOrNew() {
//            if (task.name.value.isNotEmpty() && tasks.lastOrNull() == task) {
            app.createTask(Task("", date), focus = true)
//            } else focusManager.moveFocus(FocusDirection.Down)
        }
        Task(
            task,
            onNameChange = {
                println("Updating to $it")
                task.name.value = it
            },
            onKeyEvent = { event ->
                when {
                    event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.E -> {
                        task.highlight.update {
                            Highlights.entries[(it.ordinal + 1) % Highlights.entries.size]
                        }
                        true
                    }

//                    event.key == Key.Enter && tasks.lastOrNull() == task -> {
//                        nextTaskOrNew()
//                        true
//                    }

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
