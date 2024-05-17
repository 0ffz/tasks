package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.stateholder.TaskInteractions
import me.dvyy.tasks.stateholder.TasksStateHolder
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.elements.task.ReorderableTask

//data class DNDInteractions(
//    val onDragEnterColumn: (dateState: Task, state: DraggedItemState<TaskState>) -> Unit,
//)
data class TaskListInteractions(
    val createNewTask: () -> Unit,
)

data class TaskWithIDState(
    val state: TaskState,
    val uuid: Uuid,
    val interactions: TaskInteractions,
)

@Composable
fun TaskList(
    title: TaskListKey,
    tasks: List<TaskWithIDState>,
//    date: LocalDate,
    colored: Boolean,
//    reorderState: ReorderState<TaskState>,
//    onDragEnterColumn: (dateState: DateState, state: DraggedItemState<TaskState>) -> Unit,
    fullHeight: Boolean,
    interactions: TaskListInteractions,
    tasksStateHolder: TasksStateHolder,
//    interactions: DNDInteractions,
//    modifier: Modifier = Modifier,
) {
//    val app = LocalAppState
//    val producedState by produceState<DateState?>(initialValue = app.getDateIfLoaded(date), date) {
//        value = null
//        value = app.getOrLoadDate(date)
//    }
    val ui = LocalUIState.current

    Column(Modifier.animateContentSize().fillMaxWidth()) {
        DayTitle(title, colored)
        Column(
            modifier = Modifier.padding(vertical = 8.dp).heightIn(max = 1000.dp)
//                .dropTarget(
//                    key = taskListID,
//                    state = reorderState.dndState,
//                    dropAnimationEnabled = false,
//                    onDragEnter = { interactions.onDragEnterColumn(taskListID, it) },
//                )
        ) {
            Column {
                // Queue save when task deleted
//                LaunchedEffect(tasks) {
//                    app.queueSaveDay(state)
//                }
                val selectedTask by tasksStateHolder.selectedTask.collectAsState()
                LazyColumn {
                    items(tasks, key = { it.uuid }) { task ->
                        ReorderableTask(
                            task = task.state,
                            interactions = task.interactions,
                            selected = selectedTask == task.uuid,
                        )
                    }
                }
                val emptySpace = remember(fullHeight) {
                    /*if (fullHeight) Modifier.fillMaxHeight() else */Modifier.height(ui.taskHeight)
                }
                Column(emptySpace.clickableWithoutRipple {
                    if (tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                        interactions.createNewTask()
                }) {
                    Spacer(modifier = Modifier.height(ui.taskHeight))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
