package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
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
    modifier: Modifier = Modifier,
) {
    val app = LocalAppState
    val state = remember(date) { app.loadDate(date) }

    Column(modifier.animateContentSize().fillMaxWidth()) {
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
