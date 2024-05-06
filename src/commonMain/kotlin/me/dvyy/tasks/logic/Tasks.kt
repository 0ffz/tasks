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

    // TODO pass task, return state, private constructor in state?
    fun AppState.createTask(state: TaskState) {
        tasks[state.uuid] = state
        loadDate(state.date.value).tasks.update { it + state }
    }
}
