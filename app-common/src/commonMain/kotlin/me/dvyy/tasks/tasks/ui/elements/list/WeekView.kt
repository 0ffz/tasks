package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.*
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
import com.benasher44.uuid.uuid4
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.koinInject

@Composable
fun WeekView(
    tasksStateHolder: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinInject(),
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
//    SelectTaskWhenRequested()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderInteractions = tasksStateHolder.reorderInteractions()
        ReorderContainer(state = reorderInteractions.draggedState) {
            val responsive = LocalUIState.current
            val columns = responsive.dateColumns
            val scrollModifier =
                if (responsive.appScrollable) Modifier.verticalScroll(scrollState)
                else Modifier
            val weekStart by time.weekStart.collectAsState()

            BoxWithConstraints {
                val halfHeight = this.constraints.maxHeight.dp / 2
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
                        val key = TaskListKey.Date(day)
                        val tasks by tasksStateHolder.tasksFor(key).collectAsState()
                        TaskList(
                            key = TaskListKey.Date(day),
                            tasks = tasks,
                            colored = isToday,
                            viewModel = tasksStateHolder,
                            reorderInteractions = reorderInteractions,
                            interactions = tasksStateHolder.listInteractionsFor(key),
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
    tasksStateHolder: TasksViewModel = viewModel(),
) {
    val ui = LocalUIState.current
    val projects by tasksStateHolder.projects.collectAsState(emptyList()) //TODO loading
    LazyRow(modifier) {
        items(projects) { key ->
            val tasks by tasksStateHolder.tasksFor(key).collectAsState()
            TaskList(
                key = key,
                tasks = tasks,
                viewModel = tasksStateHolder,
                reorderInteractions = reorderInteractions,
                interactions = tasksStateHolder.listInteractionsFor(key),
                modifier = Modifier
                    .width(ui.taskListWidth),
                scrollable = true
            )
        }
        item {
            Button(onClick = { tasksStateHolder.createProject(uuid4().toString()) }) {
                Text("New project")
            }
        }
    }
}
