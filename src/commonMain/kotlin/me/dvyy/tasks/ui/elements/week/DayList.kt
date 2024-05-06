package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.*
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

@OptIn(ExperimentalFoundationApi::class)
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

        val tasks by state.tasks.collectAsState()

        Column(
            modifier = Modifier.padding(vertical = 8.dp).heightIn(max = 1000.dp)
                .dropTarget(
                    key = state.date,
                    state = reorderState.dndState,
                    dropAnimationEnabled = false,
                    onDragEnter = { onDragEnterColumn(state, it) },
                )
        ) {
            LazyColumn(state = lazyListState) {
                items(tasks, key = { it.uuid }) { task ->
                    ReorderableItem(
                        state = reorderState,
                        key = task,
                        data = task,
                        zIndex = 1f,
                        onDragEnter = {
                            onDragEnterItem(task, it)
                        },
                    ) {
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

                                    event.key == Key.Enter && tasks.last() == task && task.name.value.isNotEmpty() -> {
                                        app.createTask(Task("", date), focus = true)
                                        true
                                    }

                                    else -> false
                                }

                            },
                            modifier = Modifier.alpha(if (isDragging) 0f else 1f)
                        )
                    }
                    HorizontalDivider()
                }
            }
            val emptySpace = remember(fullHeight) {
                if (fullHeight) Modifier.fillMaxHeight() else Modifier.height(40.dp)
            }
            Box(modifier = emptySpace.fillMaxWidth().onClick {
                if (tasks.last().name.value.isNotEmpty())
                    app.createTask(Task("", state.date), focus = true)
            })
        }
    }
}
