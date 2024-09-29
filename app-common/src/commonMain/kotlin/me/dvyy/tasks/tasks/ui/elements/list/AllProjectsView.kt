package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.tasks.ui.TasksViewModel

@Composable
fun AllProjectsView(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
    staggered: Boolean,
) {
    val ui = LocalUIState.current
    val projects by tasksViewModel.projects.collectAsState()
    ProjectLayout(modifier, staggered, projects) { key ->
        val properties by tasksViewModel.getListProperties(key).collectAsState()
        Project(
            tasksViewModel = tasksViewModel,
            key = key,
            properties = properties,
            modifier = Modifier.width(ui.taskListWidth),
            scrollable = false
        )
    }
}

@Composable
private fun <T> ProjectLayout(
    modifier: Modifier = Modifier,
    staggered: Boolean,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
) {
    val ui = LocalUIState.current
    when {
        staggered -> LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Adaptive(ui.taskListWidth)
        ) {
            items(items) { itemContent(it) }
        }

        else -> LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(ui.taskListWidth)
        ) {
            items(items) { itemContent(it) }
        }
    }
}
