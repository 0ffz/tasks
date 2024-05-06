package me.dvyy.tasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.changeDate
import me.dvyy.tasks.state.Constants
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.DayList

@Composable
fun HomeScreen() {
    WeekView()
}

@Composable
fun WeekView() {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val weekStart = today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    val app = LocalAppState

    BoxWithConstraints(Modifier.padding(8.dp)) {
        val columns = remember(constraints) { if (constraints.maxWidth < Constants.WEEK_VIEW_MIN_WIDTH) 1 else 7 }
        val reorderState = rememberReorderState<TaskState>()
        ReorderContainer(state = reorderState) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                fun isToday(index: Int) = index == today.dayOfWeek.ordinal
                items(7) { dayIndex ->
                    val day = weekStart.plus(DatePeriod(days = dayIndex))
                    DayList(
                        day,
                        isToday = isToday(dayIndex),
                        fullHeight = columns != 1,
                        reorderState = reorderState,
                        onDragEnterColumn = { date, state ->
                            println("Entered column ${date.date}")
                            val task = state.data
                            Tasks.singleThread.launch {
                                task.changeDate(app, date.date)
                            }
                        },
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

                                targetDate.tasks.update { tasks ->
                                    tasks.toMutableList().apply {
                                        val index = indexOf(target)
                                        println("Index was $index, tasks $this")
                                        remove(task)
                                        add(index, task)
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
