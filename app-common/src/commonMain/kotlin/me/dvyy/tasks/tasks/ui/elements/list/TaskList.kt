package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.state.Loadable
import me.dvyy.tasks.app.ui.state.loadedOrNull
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.CachedUpdate
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask

@OptIn(ExperimentalDndApi::class)
@Composable
fun TaskList(
    listId: ListId,
    tasks: Loadable<List<TaskWithIDState>>, //TODO represent loading state explicitly?
    properties: Loadable<TaskListProperties>,
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
                6.dp,
            ).fillMaxWidth()
    ) {
        val isLoading = tasks is Loadable.Loading
        TaskListTitle(
            properties,
            colored,
            interactions,
            loading = isLoading,
            key = listId,
        )
        val tasks = tasks.loadedOrNull() ?: return@Column
        val scrollState = rememberScrollState()
        val scrollModifier =
            if (scrollable) Modifier.verticalScroll(scrollState)
            else Modifier

        Column(
            modifier = Modifier.padding(vertical = 8.dp)
                .dropTarget(
                    key = listId,
                    state = reorderInteractions.draggedState.dndState,
                    dropAnimationEnabled = false,
                    onDragEnter = { reorderInteractions.onDragEnterColumn(listId, it) },
                )
        ) {
            val selectedTask by viewModel.selectedTask.collectAsState()
            Column(scrollModifier) {
                tasks.forEachIndexed { index, task ->
                    key(task.uuid) {
                        val selected = selectedTask == task.uuid
//                        val onChange = remember(task) { getInteractions(task) }::onTaskChanged
                        // cached task is the SSOT in this context, some things like text updates take too long to update in db
                        CachedUpdate(
                            key = task.uuid,
                            value = task.state,
                            onValueChanged = { viewModel.onTaskChanged(task.uuid, it) }
                        ) { cachedTask, setTask ->
                            val taskInteractions =
                                remember(cachedTask) { viewModel.interactionsFor(task.uuid, listId, cachedTask) }
                            ReorderableTask(
                                key = task.uuid,
                                task = cachedTask,
                                setTask = setTask,
                                reorderInteractions = reorderInteractions,
                                interactions = taskInteractions,
                                selected = selected,
                            )
                        }
                    }
                }
                Column(Modifier.clickableWithoutRipple {
                    if (tasks.lastOrNull()?.state?.text?.isEmpty() != true)
                        interactions.createNewTask()
                }) {
                    Spacer(modifier = Modifier.height(ui.taskHeight))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
            val fullHeight = !ui.isSingleColumn
            if (fullHeight) Box(Modifier.fillMaxSize().clickableWithoutRipple {
                if (tasks.lastOrNull()?.state?.text?.isEmpty() != true)
                    interactions.createNewTask()
            })
        }
    }
}
