package me.dvyy.tasks.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.SyncStatus
import me.dvyy.tasks.model.Task
import me.dvyy.tasks.ui.elements.week.Highlight

@Stable
class TaskState(
    val uuid: Uuid,
    name: String,
    date: LocalDate,
    syncStatus: SyncStatus,
    completed: Boolean,
    highlight: Highlight,
) {
    val syncStatus = MutableStateFlow(syncStatus)
    val name = MutableStateFlow(name)
    val date = MutableStateFlow(date)
    val completed = MutableStateFlow(completed)
    val highlight = MutableStateFlow(highlight)
    val focusRequested = MutableStateFlow(false)

    @Composable
    fun isActive(app: AppState) = app.selectedTask
        .map { it == this }
        .distinctUntilChanged()
        .collectAsState(false)

    fun toTask(): Task = Task(
        uuid = uuid,
        name = name.value,
        completed = completed.value,
        syncStatus = syncStatus.value,
    )
}
