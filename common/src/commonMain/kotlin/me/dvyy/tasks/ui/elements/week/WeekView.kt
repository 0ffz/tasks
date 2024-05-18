package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderInteractions = tasksStateHolder.reorderInteractions()
        ReorderContainer(state = reorderInteractions.draggedState) {
            val responsive = LocalUIState.current
            val columns = responsive.dateColumns
            val isSmallScreen = responsive.atMostSmall
            val scrollModifier = remember(isSmallScreen) {
                if (isSmallScreen) Modifier.verticalScroll(scrollState) else Modifier
            }
//            CompositionLocalProvider(LocalTaskReorder provides reorder) {
            val weekStart by app.time.weekStart.collectAsState()
            Column {
                NonlazyGrid(
                    columns = columns,
                    itemCount = 7,
                    modifier = scrollModifier.fillMaxWidth()
                ) { dayIndex ->
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    val isToday = day == app.time.today
                    val key = TaskListKey.Date(day)
                    val tasks by tasksStateHolder.tasksFor(key).collectAsState()
                    TaskList(
                        key = TaskListKey.Date(day),
                        tasks = tasks,
                        colored = isToday,
//                            reorderState = reorderState,
//                            onDragEnterColumn = { date, state ->
//                                println("Entered column ${date.date}")
//                                val task = state.data
//                                Tasks.singleThread.launch {
//                                    task.changeDate(app, date.date)
//                                }
//                            },
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
//                    }
                }
            }
        }
    }
}
