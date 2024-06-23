package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.core.ui.debug.LogCompositions
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.sync.ui.SyncButton
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.koinInject

@Composable
fun WeekView(
    tasksViewModel: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinInject(),
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
//    SelectTaskWhenRequested()
    Scaffold(
        floatingActionButton = { SyncButton() },
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderInteractions = tasksViewModel.reorderInteractions()
        ReorderContainer(state = reorderInteractions.draggedState) {
            val responsive = LocalUIState.current
            val columns = responsive.dateColumns
            val scrollModifier =
                if (responsive.appScrollable) Modifier.verticalScroll(scrollState)
                else Modifier
            val weekStart by time.weekStart.collectAsState()
            val halfHeight = 300.dp
            val restrictHeight =
                if (ui.isSingleColumn) Modifier else Modifier.height(halfHeight)

            Column {
                NonlazyGrid(
                    columns = columns,
                    itemCount = 7,
                    modifier = scrollModifier.fillMaxWidth().then(restrictHeight)
                ) { dayIndex ->
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    val isToday = day == time.today
                    val listId = ListId.forDate(day)
                    val properties by tasksViewModel.getListProperties(listId).collectAsState()
                    val tasks by tasksViewModel.tasksFor(listId).collectAsState()
                    LogCompositions("Tasks")
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
                if (!ui.isSingleColumn) ProjectListContent(
                    reorderInteractions = reorderInteractions,
                    modifier = restrictHeight
                )
            }
            if (ui.isSingleColumn) ProjectsList {
                ProjectListContent(
                    reorderInteractions = reorderInteractions,
                    modifier = Modifier.height(halfHeight)
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
                modifier = Modifier
                    .width(ui.taskListWidth),
                scrollable = true
            )
        }
        item {
            Button(onClick = { tasksViewModel.createProject() }) {
                Text("New project")
            }
        }
    }
}
