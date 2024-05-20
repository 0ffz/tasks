package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.stateholder.TaskReorderInteractions
import me.dvyy.tasks.stateholder.TasksViewModel
import me.dvyy.tasks.stateholder.TasksViewModel.TaskList
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.elements.task.ReorderableTask

data class TaskListInteractions(
    val createNewTask: () -> Unit,
)

@Immutable
data class TaskWithIDState(
    val state: TaskState,
    val uuid: Uuid,
)

@OptIn(ExperimentalDndApi::class)
@Composable
fun TaskList(
    key: TaskListKey,
    tasks: TaskList, //TODO represent loading state explicitly?
    colored: Boolean = false,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskListInteractions,
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
) {
    val ui = LocalUIState.current

    Column(
        modifier/*.animateContentSize()*/
            .padding(
                start = 6.dp,
                end = 6.dp,
            ).fillMaxWidth()
    ) {
        val isLoading = tasks is TaskList.Loading
        TaskListTitle(key, colored, loading = isLoading)
        when (tasks) {
            is TaskList.Loading -> return
            is TaskList.Data -> {
                val scrollState = rememberScrollState()
                val scrollModifier =
                    if (scrollable) Modifier.verticalScroll(scrollState)
                    else Modifier

                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                        .dropTarget(
                            key = key,
                            state = reorderInteractions.draggedState.dndState,
                            dropAnimationEnabled = false,
                            onDragEnter = { reorderInteractions.onDragEnterColumn(key, it) },
                        )
                        .then(scrollModifier)
                ) {
                    val selectedTask by viewModel.selectedTask.collectAsState()
                    Column {
                        tasks.tasks.forEachIndexed { index, task ->
                            val selected = selectedTask == task.uuid
                            val taskInter = remember(task.uuid) {
                                viewModel.interactionsFor(task.uuid)
                            }
                            ReorderableTask(
                                task = task,
                                reorderInteractions = reorderInteractions,
                                interactions = taskInter,
                                selected = selected,
                            )
                        }
                    }
                    val fullHeight = !ui.isSingleColumn
                    val emptySpace = remember(fullHeight) {
                        if (fullHeight) Modifier.height(500.dp) else Modifier.height(ui.taskHeight)
                    }
                    Column(emptySpace.clickableWithoutRipple {
                        if (tasks.tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                            interactions.createNewTask()
                    }) {
                        Spacer(modifier = Modifier.height(ui.taskHeight))
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
