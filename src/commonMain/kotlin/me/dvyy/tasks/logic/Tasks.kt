package me.dvyy.tasks.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Dates.loadDate
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.TaskState

object Tasks {
    @OptIn(ExperimentalCoroutinesApi::class)
    val singleThread = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

    fun TaskState.changeDate(app: AppState, newDate: LocalDate) {
        val task = this
        singleThread.launch {
            if (date == newDate) return@launch
            app.loadedDates[date]?.tasks?.remove(task)
            app.loadedDates[newDate]?.tasks?.add(task)
            println("Changing task date: $date -> $newDate")
            date = newDate
        }
    }

    // TODO pass task, return state, private constructor in state?
    fun AppState.createTask(state: TaskState) {
        tasks[state.uuid] = state
        loadDate(state.date).tasks.add(state)
    }
}
