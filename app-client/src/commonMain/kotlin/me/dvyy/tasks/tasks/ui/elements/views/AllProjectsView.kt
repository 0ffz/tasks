package me.dvyy.tasks.tasks.ui.elements.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.Project

@Composable
fun AllProjectsView(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
    horizontal: Boolean,
    staggered: Boolean,
) {
    val ui = LocalUIState.current
    val projects by tasksViewModel.projects.collectAsState(listOf())
    ProjectLayout(modifier, horizontal, staggered, projects, { it.id }) { key ->
        val listId = key.id.asList()
        Project(listId, Modifier.width(ui.taskListWidth))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> ProjectLayout(
    modifier: Modifier = Modifier,
    horizontal: Boolean,
    staggered: Boolean,
    items: List<T>,
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    val ui = LocalUIState.current
    Box {
        val state: ScrollableState = when {
            horizontal -> {
                val state = rememberLazyListState()
                LazyRow(
                    state = state,
                    modifier = modifier,
                ) {
                    items(items, key = key) { itemContent(it) }
                }
                state
            }

            staggered -> {
                val state = rememberLazyStaggeredGridState()
                LazyVerticalStaggeredGrid(
                    state = state,
                    modifier = modifier,
                    columns = StaggeredGridCells.Adaptive(ui.taskListWidth),
                ) {
                    items(items, key = key) { itemContent(it) }
                }
                state
            }

            else -> {
                val state = rememberLazyGridState()
                LazyVerticalGrid(
                    state = state,
                    modifier = modifier,
                    columns = GridCells.Adaptive(ui.taskListWidth),
                ) {
                    items(items, key = key) { itemContent(it) }
                }
                state
            }
        }
        var scrollBy by remember { mutableStateOf(0f) }

        LaunchedEffect(scrollBy) {
            while (scrollBy != 0f) {
                state.scrollBy(scrollBy)
                delay(5)
            }
        }

        @Composable
        fun scroll(amount: Float) = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = { true },
            target = remember {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        return false
                    }

                    override fun onEntered(event: DragAndDropEvent) {
                        scrollBy = amount
                    }

                    override fun onExited(event: DragAndDropEvent) {
                        scrollBy = 0f
                    }
                }
            }
        )

        val alignment = if (horizontal) Alignment.CenterEnd else Alignment.BottomCenter
        val width = if (horizontal) 0.1f else 1f
        val height = if (horizontal) 1f else 0.1f
        Box(Modifier.align(alignment).fillMaxWidth(width).fillMaxHeight(height).then(scroll(3f)))
        Box(Modifier.fillMaxWidth(width).fillMaxHeight(height).then(scroll(-3f)))
    }
}
