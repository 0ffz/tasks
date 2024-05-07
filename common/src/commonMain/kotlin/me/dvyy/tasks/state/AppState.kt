package me.dvyy.tasks.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.dvyy.tasks.logic.Task
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.ui.elements.week.Highlights

val AppStateProvider = compositionLocalOf<AppState> { error("No local versions provided") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current

@Stable
class TaskState(
    val uuid: Long,
    name: String,
    date: LocalDate,
) {
    val name = MutableStateFlow(name)
    val date = MutableStateFlow(date)
    val completed = MutableStateFlow(false)
    val highlight = MutableStateFlow(Highlights.Unmarked)
    val focusRequested = MutableStateFlow(false)
}

data class DateState(val date: LocalDate) {
    val tasks = mutableStateListOf<TaskState>()
}

class AppState {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val tasks = mutableMapOf<Long, TaskState>()

    val loadedDates = mutableMapOf<LocalDate, DateState>()

    init {
        createTask(Task("A simple thing", today))
        createTask(Task("Another task", today))
    }
}
