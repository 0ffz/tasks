package me.dvyy.tasks.tasks.ui.elements.list

import TasksViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.utils.Loadable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Project(
    path: VaultPath,
    properties: Loadable<TaskListProperties>,
    tasksViewModel: TasksViewModel = koinViewModel(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tasks by remember { tasksViewModel.tasksFor(path) }.collectAsState()
    val reorderInteractions = TaskReorderInteractions() // TODO tasksViewModel.reorderInteractions()

    TaskList(
        listId = path,
        tasks = tasks,
        properties = properties,
        viewModel = tasksViewModel,
        reorderInteractions = reorderInteractions,
        interactions = TaskListInteractions(), //TODO tasksViewModel.listInteractionsFor(key),
        modifier = modifier,
        scrollable = scrollable
    )
}
