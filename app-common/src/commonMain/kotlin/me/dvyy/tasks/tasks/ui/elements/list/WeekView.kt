package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Splitscreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.*
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekView(
    scrollBehavior: TopAppBarScrollBehavior,
    tasksViewModel: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    prefs: PreferencesViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
    val splitHeight by prefs.splitHeight.collectAsState()
    val splitCutoff = 0.05f..0.95f
    Scaffold(
        floatingActionButton = {
            Column {
                if (splitHeight !in splitCutoff) {
                    FloatingActionButton(onClick = { prefs.splitHeight.value = 0.5f }) {
                        Icon(Icons.Outlined.Splitscreen, contentDescription = "Open week view")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderInteractions = tasksViewModel.reorderInteractions()
        ReorderContainer(state = reorderInteractions.draggedState) {
            val responsive = LocalUIState.current
            val columns = responsive.dateColumns
//            val scrollModifier =
//                if (responsive.appScrollable) Modifier.verticalScroll(scrollState)
//                else Modifier
            val weekStart by time.weekStart.collectAsState()
            val restrictHeight = Modifier.fillMaxHeight(
                when {
                    splitHeight >= splitCutoff.endInclusive -> 1f
                    splitHeight <= splitCutoff.start -> 0f
                    else -> splitHeight
                }
            )
            val datesScrollable = if (ui.isSingleColumn)
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).verticalScroll(scrollState)
            else Modifier
            var height by remember { mutableStateOf(0) }
            val today by time.today.collectAsState()
            Column(Modifier.onGloballyPositioned { height = it.size.height }) {
                if (splitHeight > splitCutoff.start) NonlazyGrid(
                    columns = columns,
                    itemCount = 7,
                    modifier = Modifier.fillMaxWidth().then(restrictHeight).then(datesScrollable),
                ) { dayIndex ->
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    val isToday = day == today
                    val listId = ListId.forDate(day)
                    val properties by tasksViewModel.getListProperties(listId).collectAsState()
                    val tasks by tasksViewModel.tasksFor(listId).collectAsState()
                    TaskList(
                        listId = listId,
                        tasks = tasks,
                        properties = properties,
                        colored = isToday,
                        viewModel = tasksViewModel,
                        reorderInteractions = reorderInteractions,
                        interactions = tasksViewModel.listInteractionsFor(listId),
                        scrollable = !ui.isSingleColumn
                    )
                }
                val scrollableState = rememberScrollableState { delta ->
                    val newSplitHeight = (splitHeight + delta / height).coerceIn(0f, 1f)
                    prefs.splitHeight.value = newSplitHeight
                    if (newSplitHeight == 0f || newSplitHeight == 1f) 0f
                    else delta
                }
                val draggableState = rememberDraggableState { delta ->
                    prefs.splitHeight.update { original -> (original + delta / height).coerceIn(0f, 1f) }
                }
                val handleModifier = if (ui.isSingleColumn) {
                    Modifier.scrollable(
                        scrollableState,
                        Orientation.Vertical,
                    )
                } else Modifier.draggable(draggableState, Orientation.Vertical)

                Box(Modifier.fillMaxWidth().then(handleModifier)) {
                    if (splitHeight in splitCutoff) DividerPill()
                }
                if (splitHeight < splitCutoff.endInclusive) ProjectListContent(
                    reorderInteractions = reorderInteractions,
                    modifier = Modifier.fillMaxHeight() //Fill remaining height
                )
            }
        }
    }
}

@Composable
fun DividerPill() {
    val ui = LocalUIState.current
    Box(
        Modifier.height(ui.dividerHeight).pointerHoverIcon(Cursors.horizontalResize),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider()
        Surface(Modifier.height(8.dp).width(220.dp)) {}
        Surface(
            shape = MaterialTheme.shapes.small,
            tonalElevation = 2.dp,
            modifier = Modifier.height(8.dp).width(200.dp)
        ) { }
    }
}

@Composable
fun ProjectListContent(
    reorderInteractions: TaskReorderInteractions,
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
) {
    val ui = LocalUIState.current
    val projects by tasksViewModel.projects.collectAsState()
    LazyRow(modifier) {
        items(projects) { key ->
            val tasks by tasksViewModel.tasksFor(key).collectAsState()
            val properties by tasksViewModel.getListProperties(key).collectAsState()
            TaskList(
                listId = key,
                tasks = tasks,
                properties = properties,
                viewModel = tasksViewModel,
                reorderInteractions = reorderInteractions,
                interactions = tasksViewModel.listInteractionsFor(key),
                modifier = Modifier.width(ui.taskListWidth),
                scrollable = true
            )
        }
        item {
//            Column(modifier = Modifier.width(ui.taskListWidth)) {
//                TaskListTitle(
//                    TaskListProperties(displayName = "").loaded(),
//                    false,
//                    TaskListInteractions(
//                        onPropertiesChanged = { }
//                    ),
//                )
//            }
//            Box(
//                contentAlignment = Alignment.Center,
//            ) {
            FilledTonalButton(
                modifier = Modifier.width(ui.taskListWidth),
                onClick = { tasksViewModel.createProject() },
            ) {
                Text("New project")
            }
//            }
        }
    }
}
