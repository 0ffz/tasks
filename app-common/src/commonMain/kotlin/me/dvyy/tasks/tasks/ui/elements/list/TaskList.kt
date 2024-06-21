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
                start = 6.dp,
                end = 6.dp,
            ).fillMaxWidth()
    ) {
        val isLoading = tasks is Loadable.Loading
        TaskListTitle(
            properties,
            colored,
            interactions,
            loading = isLoading
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
                    if (tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                        interactions.createNewTask()
                }) {
                    Spacer(modifier = Modifier.height(ui.taskHeight))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
            val fullHeight = !ui.isSingleColumn
            if (fullHeight) Box(Modifier.fillMaxSize().clickableWithoutRipple {
                if (tasks.lastOrNull()?.state?.name?.isEmpty() != true)
                    interactions.createNewTask()
            })
        }
    }
}
