package me.dvyy.tasks.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.changeDate
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.LocalResponsiveUI
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.DayList
import me.dvyy.tasks.ui.elements.week.LocalTaskReorder
import me.dvyy.tasks.ui.elements.week.NonlazyGrid
import me.dvyy.tasks.ui.elements.week.TaskReorder

@Composable
fun HomeScreen() {
    Column(Modifier.padding(8.dp)) {
        WeekView()
    }
}

@Composable
fun WeekView() {
    val app = LocalAppState
    val scrollState = rememberScrollState()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val reorderState = rememberReorderState<TaskState>()
        ReorderContainer(state = reorderState) {
            val responsive = LocalResponsiveUI.current
            val columns = responsive.dateColumns
            val isSmallScreen = responsive.atMostSmall
            val scrollModifier = remember(isSmallScreen) {
                if (isSmallScreen) Modifier.verticalScroll(scrollState) else Modifier
            }
            val reorder = remember {
                TaskReorder(
                    state = reorderState,
                    onDragEnterItem = { target, state ->
                        val task = state.data

                        Tasks.singleThread.launch {
                            if (target == task) return@launch
                            val targetDate = app.loadedDates[target.date.value] ?: return@launch
                            val taskDate = app.loadedDates[task.date.value]
                            println("Reordering in target ${targetDate}, task: ${taskDate},")

                            if (taskDate != targetDate) {
                                task.changeDate(app, targetDate.date)
                            }

                            targetDate.tasks.emit(
                                targetDate.tasks.value.toMutableList().apply {
                                    val index = indexOf(target)
                                    println("Index was $index, tasks ${this.map { it.name.value }}")
                                    if (index == -1) return@launch
                                    remove(task)
                                    add(index, task)
                                }
                            )
                        }
                    })
            }
            CompositionLocalProvider(LocalTaskReorder provides reorder) {
                val weekStart by app.weekStart.collectAsState()
                LaunchedEffect(weekStart) {
                    app.loadTasksForWeek()
                }
//                val padding = WindowInsets.ime
//                val density = LocalDensity.current
//                LaunchedEffect(density) {
//                    println(padding)
//                    var prev = 0f
//                    snapshotFlow { padding.getBottom(density) }
//                        .collectLatest {
//                            scrollState.scrollBy((it - prev))
//                            println("Padding ${it - prev}")
//                            prev = it.toFloat()
//                        }
//                }
                Column {
                    NonlazyGrid(
                        columns = columns,
                        itemCount = 7,
                        modifier = scrollModifier.fillMaxWidth()
                    ) { dayIndex ->
                        val day = weekStart.plus(DatePeriod(days = dayIndex))
                        DayList(
                            day,
                            isToday = day == app.today,
                            reorderState = reorderState,
                            onDragEnterColumn = { date, state ->
                                println("Entered column ${date.date}")
                                val task = state.data
                                Tasks.singleThread.launch {
                                    task.changeDate(app, date.date)
                                }
                            },
                            fullHeight = !isSmallScreen,
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
}

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(msg: String) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    println("Compositions: $msg ${ref.value}")
}
