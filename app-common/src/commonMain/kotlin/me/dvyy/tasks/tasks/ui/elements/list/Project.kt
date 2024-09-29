package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.utils.Loadable

@Composable
fun Project(
    key: ListId,
    properties: Loadable<TaskListProperties>,
    tasksViewModel: TasksViewModel = viewModel(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tasks by tasksViewModel.tasksFor(key).collectAsState()
    val reorderInteractions = tasksViewModel.reorderInteractions()

    TaskList(
        listId = key,
        tasks = tasks,
        properties = properties,
        viewModel = tasksViewModel,
        reorderInteractions = reorderInteractions,
        interactions = tasksViewModel.listInteractionsFor(key),
        modifier = modifier,
        scrollable = scrollable
    )
}
