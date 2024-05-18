package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.stateholder.TasksViewModel

@Composable
fun WeekView(tasksStateHolder: TasksViewModel = viewModel()) {
    val app = LocalAppState
    val scrollState = rememberScrollState()
    Box {
        // Always play the selection animation when a task is created
        // by emitting a state update after the UI composition. TODO is there a better way?
        val requestedSelect by tasksStateHolder.requestedSelectTask.collectAsState()
        LaunchedEffect(requestedSelect) {
            tasksStateHolder.selectedTask.emit(requestedSelect)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderInteractions = tasksStateHolder.reorderInteractions()
        ReorderContainer(state = reorderInteractions.draggedState) {
            val responsive = LocalUIState.current
            val columns = responsive.dateColumns
            val scrollModifier =
                if (responsive.appScrollable) Modifier.verticalScroll(scrollState)
                else Modifier
            val weekStart by app.time.weekStart.collectAsState()

            Column {
                NonlazyGrid(
                    columns = columns,
                    itemCount = 7,
                    modifier = scrollModifier.fillMaxWidth()
                ) { dayIndex ->
                    println("Recomposing in nonlazygrid")
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    val isToday = day == app.time.today
                    val key = TaskListKey.Date(day)
                    val tasks by tasksStateHolder.tasksFor(key).collectAsState()
                    TaskList(
                        key = TaskListKey.Date(day),
                        tasks = tasks,
                        colored = isToday,
                        tasksStateHolder = tasksStateHolder,
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
            }
        }
    }
}
