package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Dates.loadDate
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
) {
    val lazyListState = rememberLazyListState()
    val app = LocalAppState
    val state = remember(date) { app.loadDate(date) }

    Column(
        Modifier.dropTarget(
            key = state.date,
            state = reorderState.dndState,
            dropAnimationEnabled = false,
            onDragEnter = { onDragEnterColumn(state, it) },
        )
    ) {
        DayTitle(state.date, isToday)
        val tasks by state.tasks.collectAsState()
        LazyColumn(
            state = lazyListState, modifier = Modifier.padding(16.dp).heightIn(max = 1000.dp)
        ) {
            items(tasks, key = { it.uuid }) { task ->
                var highlight by remember { mutableStateOf(Highlights.Unmarked) }
                val highlightAnimate by animateColorAsState(highlight.color)

                ReorderableItem(
                    state = reorderState,
                    key = task,
                    data = task,
                    zIndex = 1f,
                    onDragEnter = { onDragEnterItem(task, it) },
                ) {
                    Task(
                        task,
                        onNameChange = { /* TODO task.text = it*/ },
                        highlight = highlightAnimate,
                        onTab = {
                            highlight =
                                Highlights.entries[(highlight.ordinal + 1) % Highlights.entries.size]
                        },
                        modifier = Modifier.alpha(if (isDragging) 0f else 1f)
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
