package me.dvyy.tasks.layout.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.FileDescription
import dev.seyfarth.tablericons.outlined.LayoutCards
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.layout.ui.screens.builder.screen
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import org.kodein.di.compose.viewmodel.rememberViewModel

fun projectScreen(screen: ScreenDest.Project) = screen(
    icon = TablerIcons.Outlined.FileDescription,
    tabLabel = {
        val tasks: TasksViewModel by rememberViewModel()
        val title = tasks.watchProjectTitle(screen.id.uuid).collectAsState(initial = null).value?.title
//    val icon = when {
//        //TODO reimplement
////                    props.displayName?.contains(emojiRegex) == true -> null
////                    props.displayName == "Inbox" -> AppIcons.Inbox
//        else -> TablerIcons.Outlined.FileDescription
//    }
        androidx.compose.ui.text.AnnotatedString(title ?: "No title")
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Box(Modifier.weight(1f)) {
//            Text(icon, title ?: "Untitled")
//        }
//        if (location == Location.Sidebar) {
//            BoxButton(
//                onClick = { TODO("Open project remove dialog") /*dialogs.show(AppDialog.ConfirmDeleteProject(key))*/ },
//            ) {
//                Icon(TablerIcons.Outlined.Trash, "Delete project", tint = MaterialTheme.colorScheme.outline)
//            }
//        }
//    }
    }) {
    Project(screen.id, modifier = Modifier, displayOptions = rememberProjectDisplayOptions(scrollable = true, fullHeight = true))
}

fun allProjectsScreen(
    screen: ScreenDest.Projects,
) = screen(
    icon = TablerIcons.Outlined.LayoutCards,
    tabLabel = { AnnotatedString("All projects") }
) {
    AllProjectsScreen(screen.horizontal, screen.staggered, screen.projects)
}

@Composable
private fun AllProjectsScreen(
    horizontal: Boolean,
    staggered: Boolean,
    projects: ImmutableList<ListId>? = null,
) {
    val tasksViewModel by rememberViewModel<TasksViewModel>()
    val ui = LocalUIState.current
    val projectList by tasksViewModel.projects.collectAsStateWithLifecycle()
    val projects = remember(projects, projectList) { projects ?: projectList.map { it.id.asList() }.toImmutableList() }
    ProjectLayout(horizontal, staggered, items = projects, key = { it.uuid }, itemContent = { listId ->
        Project(
            listId,
            Modifier.width(ui.taskListWidth),
            displayOptions = rememberProjectDisplayOptions(
                scrollable = horizontal,
                fullHeight = horizontal,
            )
        )
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> ProjectLayout(
    horizontal: Boolean,
    staggered: Boolean,
    items: ImmutableList<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
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

        val alignment = if (horizontal) Alignment.CenterEnd else Alignment.BottomCenter
        val width = if (horizontal) 0.1f else 1f
        val height = if (horizontal) 1f else 0.1f
        Box(Modifier.align(alignment).fillMaxWidth(width).fillMaxHeight(height))
        Box(Modifier.fillMaxWidth(width).fillMaxHeight(height))
    }
}
