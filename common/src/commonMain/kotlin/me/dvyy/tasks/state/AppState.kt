package me.dvyy.tasks.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.platforms.PersistentStore
import me.dvyy.tasks.serialization.Task
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.week.Highlight

val AppStateProvider = compositionLocalOf<AppState> { error("No local versions provided") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current

@Stable
class TaskState(
    val uuid: Uuid,
    name: String,
    date: LocalDate,
) {
    val name = MutableStateFlow(name)
    val date = MutableStateFlow(date)
    val completed = MutableStateFlow(false)
    val highlight = MutableStateFlow(Highlight.Unmarked)
    val focusRequested = MutableStateFlow(false)

    @Composable
    fun isActive(app: AppState) = app.selectedTask
        .map { it == this }
        .distinctUntilChanged()
        .collectAsState(false)
}

@Stable
data class DateState(val date: LocalDate) {
    val tasks = MutableStateFlow(listOf<TaskState>())
}

@Stable
class AppState {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val weekStart = today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    val store = PersistentStore()

    val tasks = mutableMapOf<Uuid, TaskState>()
    val selectedTask = MutableStateFlow<TaskState?>(null)
    val isSmallScreen = MutableStateFlow(false)
    val loadedDates = mutableMapOf<LocalDate, DateState>()

    init {
        (0..6).map { weekStart.plus(DatePeriod(days = it)) }
            .forEach { day ->
                val state = DateState(day)
                val tasks = store.loadTasksForDay(day)
                tasks.forEach { state.createTask(this, it) }
                loadedDates[day] = state
            }
    }

    fun saveTasks() {
        loadedDates.values.forEach { state -> saveDay(state) }
    }

    fun saveDay(state: DateState) {
        store.saveDay(state.date, state.tasks.value.map {
            Task(
                uuid = it.uuid,
                name = it.name.value,
                completed = it.completed.value
            )
        })
    }

    private var saveQueued: Boolean = false
    private val daysToSave = mutableSetOf<DateState>()

    fun queueSaveDay(state: DateState) {
        Tasks.singleThread.launch {
            daysToSave.add(state)
            if (saveQueued) return@launch
            saveQueued = true
            delay(AppConstants.bufferTaskSaves)
            daysToSave.forEach { saveDay(it) }
            daysToSave.clear()
            saveQueued = false
        }
    }
}
