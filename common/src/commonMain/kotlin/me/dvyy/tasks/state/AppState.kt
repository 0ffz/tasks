package me.dvyy.tasks.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.platforms.PersistentStore
import me.dvyy.tasks.sync.SyncClient
import me.dvyy.tasks.ui.AppConstants

val AppStateProvider = compositionLocalOf<AppState> { error("No local versions provided") }

val LocalAppState: AppState
    @Composable get() = AppStateProvider.current

@Stable
class AppState {
    val timezone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timezone).date
    val weekStart = today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    val store = PersistentStore()

    val tasks = mutableMapOf<Uuid, TaskState>()
    val selectedTask = MutableStateFlow<TaskState?>(null)
    val isSmallScreen = MutableStateFlow(false)
    val loadedDates = mutableMapOf<LocalDate, DateState>()

    val sync = SyncClient("http://localhost:4000", this)
    val snackbarHostState = SnackbarHostState()

    init {
        Tasks.singleThread.launch {
            (0..6).map { weekStart.plus(DatePeriod(days = it)) }
                .forEach { day ->
                    val state = DateState(day)
                    store.loadTasksForDay(day)
                        .onSuccess { it.forEach { state.createTask(this@AppState, it) } }
                        .onFailure { it.printStackTrace() }
                    loadedDates[day] = state
                }
//                sync.sync()
        }
    }

    fun saveTasks() {
        loadedDates.values.forEach { state -> saveDay(state) }
    }

    fun saveDay(state: DateState) {
        store.saveDay(state.date, state.tasks.value.map {
            it.toTask()
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
