package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
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
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TimeState
import me.dvyy.tasks.stateholder.TasksViewModel
import org.koin.compose.koinInject

@Composable
fun WeekView(
    tasksStateHolder: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeState = koinInject(),
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
    SelectTaskWhenRequested()
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
                Column {
                    val halfHeight = this@BoxWithConstraints.constraints.maxHeight.dp / 2
                    NonlazyGrid(
                        columns = columns,
                        itemCount = 7,
                        modifier = scrollModifier.fillMaxWidth().height(halfHeight)
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
                            modifier = Modifier
                                .padding(
                                    start = 6.dp,
                                    end = 6.dp,
                                    bottom = if (dayIndex == 6) 500.dp else 0.dp
                                )
                        )
                    }
                    val projects by tasksStateHolder.projects.collectAsState(emptyList()) //TODO loading
                    LazyRow(Modifier.height(halfHeight)) {
                        items(projects) { key ->
                            val tasks by tasksStateHolder.tasksFor(key).collectAsState()
                            TaskList(
                                key = key,
                                tasks = tasks,
                                viewModel = tasksStateHolder,
                                reorderInteractions = reorderInteractions,
                                interactions = tasksStateHolder.listInteractionsFor(key),
                                modifier = Modifier
                                    .width(ui.taskListWidth)
                            )
                        }
                        item {
                            Button(onClick = { tasksStateHolder.createProject(uuid4().toString()) }) {
                                Text("New project")
                            }
                        }
                    }
                }
            }
        }
    }
}
