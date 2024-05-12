package me.dvyy.tasks.logic

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.SyncStatus
import me.dvyy.tasks.model.Task
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.Highlight

object Tasks {
    @OptIn(ExperimentalCoroutinesApi::class)
    val singleThread = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

    fun TaskState.changeDate(app: AppState, newDate: LocalDate) {
        val task = this
        val date = date.value
        if (date == newDate) return
        app.loadedDates[date]?.tasks?.update { it - task }
        app.loadedDates[newDate]?.tasks?.update { it + task }
        println("Changing task date: $date -> $newDate")
        this.date.update { newDate }
    }


    fun getTaskUUID() = uuid4()

    fun DateState.createEmptyTask(app: AppState, focus: Boolean = false): TaskState {
        return createTask(
            app,
            Task(
                getTaskUUID(),
                name = "",
                completed = false,
                syncStatus = SyncStatus.LOCAL_ONLY,
            ),
            focus
        )!! // UUID clash is effectively impossible
    }

    fun DateState.createTask(
        app: AppState,
        task: Task,
        focus: Boolean = false,
        updateDateTaskList: Boolean = true,
    ): TaskState? {
        val state = TaskState(
            uuid = task.uuid,
            name = task.name,
            date = date,
            syncStatus = task.syncStatus,
            completed = task.completed,
            highlight = Highlight.Unmarked, //TODO store highlight
        )
        if (app.tasks.contains(task.uuid)) return null
        app.tasks[state.uuid] = state
        if (updateDateTaskList) tasks.update { it + state }
        if (focus) {
            app.selectedTask.value = state
            state.focusRequested.value = true
        }
        return state
    }

    fun TaskState.delete(app: AppState) {
        app.loadedDates[date.value]?.tasks?.update { it - this }
        app.tasks.remove(uuid)
        app.sync.diffRemoved.add(uuid)
    }
}

