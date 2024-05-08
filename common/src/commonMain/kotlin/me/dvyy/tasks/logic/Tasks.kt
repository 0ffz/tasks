package me.dvyy.tasks.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Dates.loadDate
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.TaskState

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

    private var id = 0L

    // TODO pass task, return state, private constructor in state?
    fun AppState.createTask(task: Task, focus: Boolean = false): TaskState {
        val state = TaskState(id++, task.name, task.date)
        tasks[state.uuid] = state
        loadDate(task.date).tasks.update { it + state }
        if (focus) {
            selectedTask.value = state
            state.focusRequested.value = true
        }
        return state
    }

    fun TaskState.delete(app: AppState) {
        app.loadedDates[date.value]?.tasks?.update { it - this }
        app.tasks.remove(uuid)
    }
}

class Task(val name: String, val date: LocalDate)
