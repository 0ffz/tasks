package me.dvyy.tasks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.changeDate
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.elements.sync.SyncButton
import me.dvyy.tasks.ui.elements.week.DayList
import me.dvyy.tasks.ui.elements.week.LocalTaskReorder
import me.dvyy.tasks.ui.elements.week.NonlazyGrid
import me.dvyy.tasks.ui.elements.week.TaskReorder

@Composable
fun HomeScreen() {
    WeekView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekView() {
    val app = LocalAppState
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                var toggled by remember { mutableStateOf(false) }
                SyncButton(snackbarHostState)
                AnimatedVisibility(toggled) {
                    SmallFloatingActionButton(onClick = {}) {
                        Icon(Icons.Rounded.Cloud, contentDescription = "Server setup")
                    }
                }
                FloatingActionButton(onClick = { toggled = !toggled }) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        BoxWithConstraints(
            Modifier
                .padding(horizontal = 8.dp)
                .clickableWithoutRipple { app.selectedTask.value = null }
        ) {
            LaunchedEffect(constraints) {
                app.isSmallScreen.emit(constraints.maxWidth < AppConstants.VIEW_SMALL_MAX_WIDTH)
            }
            val reorderState = rememberReorderState<TaskState>()
            ReorderContainer(state = reorderState) {
                val isSmallScreen by app.isSmallScreen.collectAsState()
                val columns = remember(isSmallScreen) { if (isSmallScreen) 1 else 7 }
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

                                targetDate.tasks.update {
                                    it.toMutableList().apply {
                                        val index = indexOf(target)
                                        println("Index was $index, tasks ${this.map { it.name.value }}")
                                        remove(task)
                                        add(index, task)
                                    }
                                }
                            }
                        })
                }
                CompositionLocalProvider(LocalTaskReorder provides reorder) {
                    NonlazyGrid(
                        columns = columns,
                        itemCount = 7,
                        modifier = scrollModifier.fillMaxSize()
                    ) { dayIndex ->
                        fun isToday(index: Int) = index == app.today.dayOfWeek.ordinal
                        val day = app.weekStart.plus(DatePeriod(days = dayIndex))
                        DayList(
                            day,
                            isToday = isToday(dayIndex),
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
                                .padding(8.dp)
                                .padding(bottom = if (dayIndex == 6) 200.dp else 0.dp)
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
