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
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.stateholder.TaskInteractions
import me.dvyy.tasks.stateholder.TaskReorderInteractions
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

@OptIn(ExperimentalDndApi::class)
@Composable
fun TaskList(
    key: TaskListKey,
    tasks: List<TaskWithIDState>?, //TODO represent loading state explicitly?
    colored: Boolean,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskListInteractions,
    tasksStateHolder: TasksStateHolder,
    modifier: Modifier = Modifier,
) {
//    val app = LocalAppState
//    val producedState by produceState<DateState?>(initialValue = app.getDateIfLoaded(date), date) {
//        value = null
//        value = app.getOrLoadDate(date)
//    }
    val ui = LocalUIState.current

    Column(
        modifier.animateContentSize()
            .fillMaxWidth()
            .dropTarget(
                key = key,
                state = reorderInteractions.draggedState.dndState,
                dropAnimationEnabled = false,
                onDragEnter = { reorderInteractions.onDragEnterColumn(key, it) },
            )
    ) {
        TaskListTitle(key, colored, loading = tasks == null)
        if (tasks == null) return
        Column(
            modifier = Modifier.padding(vertical = 8.dp).heightIn(max = 1000.dp)
        ) {
            val selectedTask by tasksStateHolder.selectedTask.collectAsState()
            LazyColumn {
                items(tasks, key = { it.uuid }) { task ->
                    ReorderableTask(
                        task = task,
                        reorderInteractions = reorderInteractions,
                        selected = selectedTask == task.uuid,
                    )
                }
            }
            val fullHeight = !ui.singleColumnLists
            val emptySpace = remember(fullHeight) {
                if (fullHeight) Modifier.height(500.dp) else Modifier.height(ui.taskHeight)
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
