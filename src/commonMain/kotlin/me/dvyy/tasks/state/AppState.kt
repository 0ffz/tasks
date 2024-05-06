package me.dvyy.tasks.state

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.dvyy.tasks.logic.Tasks.createTask

val AppStateProvider = compositionLocalOf<AppState> { error("No local versions provided") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current

class TaskState(
    val uuid: Long,
    name: String,
    date: LocalDate,
) {
    var name = MutableStateFlow(name)
    var date = MutableStateFlow(date)
    var completed = MutableStateFlow(false)
}

data class DateState(val date: LocalDate) {
    val tasks = MutableStateFlow(listOf<TaskState>())
}

class AppState {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val tasks = mutableMapOf<Long, TaskState>()

    val loadedDates = mutableMapOf<LocalDate, DateState>()

    init {
        createTask(TaskState(1, "A simple thing", today))
        createTask(TaskState(2, "Another task", today))
    }
}
