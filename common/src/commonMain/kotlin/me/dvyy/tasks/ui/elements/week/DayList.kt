package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Dates.loadDate
import me.dvyy.tasks.logic.Tasks.createEmptyTask
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple

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

            LaunchedEffect(tasks) {
                app.queueSaveDay(state)
            }

            LazyColumn {
                items(tasks, key = { it.uuid }) { task ->
                    ReorderableTask(state, task, onDragEnterItem, reorderState)
                }
            }
            val emptySpace = remember(fullHeight) {
                if (fullHeight) Modifier.fillMaxHeight() else Modifier.height(AppConstants.taskHeight)
            }
            Box(modifier = emptySpace.fillMaxWidth().clickableWithoutRipple {
                if (tasks.lastOrNull()?.name?.value?.isEmpty() != true)
                    state.createEmptyTask(app, focus = true)
            }) {
                Column {
                    Spacer(modifier = Modifier.height(AppConstants.taskHeight))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
