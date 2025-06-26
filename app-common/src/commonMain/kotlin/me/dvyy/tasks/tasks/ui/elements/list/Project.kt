package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.loaded
import me.dvyy.tasks.utils.loadedOrNull

@Composable
fun Project(
    key: ListId,
    properties: Loadable<TaskListProperties>,
    tasksViewModel: TasksViewModel = viewModel(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tasks by remember(key) { tasksViewModel.watchTasksFor(key.uuid) }.collectAsState(listOf())
//    val reorderInteractions = tasksViewModel.reorderInteractions()

    //TODO reimplement
//    TaskList(
//        listId = key,
//        tasks = Loadable.Loaded(tasks),
//        properties = properties.loadedOrNull()!!,
//        viewModel = tasksViewModel,
//        reorderInteractions = TaskReorderInteractions(),
//        interactions = tasksViewModel.listInteractionsFor(key),
//        modifier = modifier,
//        scrollable = scrollable
//    )
}
