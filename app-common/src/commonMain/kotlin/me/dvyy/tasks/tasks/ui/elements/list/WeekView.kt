package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.koinInject

@Composable
fun WeekView(
    tasksViewModel: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    prefs: PreferencesViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
    var splitHeight by prefs.floatSetting("splitHeight", 0.5f)
    val splitCutoff = 0.05f..0.95f

    Scaffold(
        floatingActionButton = {
            Column {
                if (splitHeight !in splitCutoff) {
                    FloatingActionButton(onClick = { splitHeight = 0.5f }) {
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
            val scrollModifier =
                if (responsive.appScrollable) Modifier.verticalScroll(scrollState)
                else Modifier
            val weekStart by time.weekStart.collectAsState()
            val restrictHeight =
                if (ui.isSingleColumn) Modifier else Modifier.fillMaxHeight(
                    when {
                        splitHeight >= splitCutoff.endInclusive -> 1f
                        splitHeight <= splitCutoff.start -> 0f
                        else -> splitHeight
                    }
                )
            var height by remember { mutableStateOf(0) }
            Column(Modifier.onGloballyPositioned { height = it.size.height }) {
                if (splitHeight > splitCutoff.start) NonlazyGrid(
                    columns = columns,
                    itemCount = 7,
                    modifier = scrollModifier.fillMaxWidth().then(restrictHeight)
                ) { dayIndex ->
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    val isToday = day == time.today
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
                val draggableState = rememberDraggableState {
                    splitHeight = (splitHeight + it / height).coerceIn(0f, 1f)
                }
                Box(
                    Modifier.fillMaxWidth().draggable(draggableState, Orientation.Vertical),
                    contentAlignment = Alignment.Center
                ) {
                    if (splitHeight in splitCutoff) Box(Modifier.height(15.dp)) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            tonalElevation = 2.dp,
                            modifier = Modifier.height(8.dp).width(200.dp)
                        ) { }
                    }
                }
                if (!ui.isSingleColumn && splitHeight < splitCutoff.endInclusive) ProjectListContent(
                    reorderInteractions = reorderInteractions,
                    modifier = Modifier.fillMaxHeight() //Fill remaining height
                )
            }
            if (ui.isSingleColumn) ProjectsList {
                ProjectListContent(
                    reorderInteractions = reorderInteractions,
                    modifier = Modifier.fillMaxHeight(0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsList(content: @Composable () -> Unit) {
    val ui = LocalUIState.current
    if (ui.isSingleColumn) BottomSheetScaffold(sheetContent = {
        content()
    }) {
    }
    else content()
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
