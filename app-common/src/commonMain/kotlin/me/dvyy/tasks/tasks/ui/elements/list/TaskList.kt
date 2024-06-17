package me.dvyy.tasks.tasks.ui.elements.list

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
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.Task
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel.TaskList
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask

data class TaskListInteractions(
    val createNewTask: () -> Unit = {},
    val onTitleChange: (String) -> Unit = {},
)

@Immutable
data class TaskWithIDState(
    val state: Task,
    val uuid: Uuid,
)

@OptIn(ExperimentalDndApi::class)
@Composable
fun TaskList(
    key: ListKey,
    title: ListTitle,
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
        TaskListTitle(title, colored, interactions, loading = isLoading)
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
                ) {
                    val selectedTask by viewModel.selectedTask.collectAsState()
                    Column(scrollModifier) {
                        tasks.tasks.forEachIndexed { index, task ->
                            key(task.uuid) {
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
                        Column(Modifier.clickableWithoutRipple {
                            if (tasks.tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                                interactions.createNewTask()
                        }) {
                            Spacer(modifier = Modifier.height(ui.taskHeight))
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    val fullHeight = !ui.isSingleColumn
                    if (fullHeight) Box(Modifier.fillMaxSize().clickableWithoutRipple {
                        if (tasks.tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                            interactions.createNewTask()
                    })
                }
            }
        }
    }
}
