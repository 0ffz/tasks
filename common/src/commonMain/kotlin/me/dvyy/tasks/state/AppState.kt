package me.dvyy.tasks.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
    val weekStart = MutableStateFlow(today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY))
    val store = PersistentStore()

    val tasks = mutableMapOf<Uuid, TaskState>()
    val selectedTask = MutableStateFlow<TaskState?>(null)
    val isSmallScreen = MutableStateFlow(false)
    val loadedDates = mutableMapOf<LocalDate, DateState>()
    val auth = Auth()

    val sync = SyncClient("http://localhost:4000", this)
    val snackbarHostState = SnackbarHostState()
    val activeDialog = MutableStateFlow<AppDialog?>(null)
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)

    fun loadTasksForWeek() {
        loadedDates.clear()
        tasks.clear()
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

    fun nextWeek() {
        weekStart.value = weekStart.value.plus(1, DateTimeUnit.WEEK)
    }

    fun previousWeek() {
        weekStart.value = weekStart.value.minus(1, DateTimeUnit.WEEK)
    }
}
