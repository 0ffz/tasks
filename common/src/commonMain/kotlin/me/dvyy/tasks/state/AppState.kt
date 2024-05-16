package me.dvyy.tasks.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import me.dvyy.tasks.logic.Dates.getOrLoadDate
import me.dvyy.tasks.logic.Tasks
import me.dvyy.tasks.platforms.PersistentStore
import me.dvyy.tasks.sync.SyncClient

@Stable
class AppState {
    val store = PersistentStore()
    val time = TimeState()

    val tasks = mutableMapOf<Uuid, TaskState>()
    val selectedTask = MutableStateFlow<TaskState?>(null)
    val loadedDates = mutableMapOf<LocalDate, DateState>()
    val auth = Auth()

    val sync = SyncClient("http://localhost:4000", this)
    val snackbarHostState = SnackbarHostState()
    val activeDialog = MutableStateFlow<AppDialog?>(null)
    val drawerState = DrawerState(initialValue = DrawerValue.Closed)

    suspend fun loadTasksForWeek() = withContext(Tasks.ioThread.coroutineContext) {
        (0..6)
            .map { time.weekStart.value.plus(DatePeriod(days = it)) }
            .map { day ->
                async(Tasks.ioThread.coroutineContext) {
                    getOrLoadDate(day)
                }
            }
            .awaitAll()
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
