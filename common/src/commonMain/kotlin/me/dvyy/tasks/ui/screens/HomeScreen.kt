package me.dvyy.tasks.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.changeDate
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.week.DayList
import me.dvyy.tasks.ui.elements.week.NonlazyGrid

@Composable
fun HomeScreen() {
    WeekView()
}

@Composable
fun WeekView() {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val weekStart = today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    val app = LocalAppState

    BoxWithConstraints(
        Modifier.padding(8.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { app.selectedTask.value = null },
        )
    ) {
        LaunchedEffect(constraints) {
            app.isSmallScreen.emit(constraints.maxWidth < AppConstants.VIEW_SMALL_MAX_WIDTH)
        }
        val isSmallScreen by app.isSmallScreen.collectAsState()
        val columns = remember(isSmallScreen) { if (isSmallScreen) 1 else 7 }
        val reorderState = rememberReorderState<TaskState>()
        val scrollState = rememberScrollState()
        val scrollModifier = remember(isSmallScreen) {
            if (isSmallScreen) Modifier.verticalScroll(scrollState) else Modifier
        }
        ReorderContainer(state = reorderState) {
            NonlazyGrid(
                columns = columns,
                itemCount = 7,
                modifier = scrollModifier.fillMaxSize()
            ) { dayIndex ->
                fun isToday(index: Int) = index == today.dayOfWeek.ordinal
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

                            targetDate.tasks.update {
                                it.toMutableList().apply {
                                    val index = indexOf(target)
                                    println("Index was $index, tasks $this")
                                    remove(task)
                                    add(index, task)
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                )
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
