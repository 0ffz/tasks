package me.dvyy.tasks.logic

import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.logic.Tasks.createTask
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.DateState

object Dates {
    suspend fun AppState.getOrLoadDate(date: LocalDate): DateState = withContext(Tasks.ioThread.coroutineContext) {
        loadedDates.getOrPut(date) {
            DateState(date).also { state ->
                store.loadTasksForDay(date)
                    .onSuccess { it.forEach { state.createTask(this@getOrLoadDate, it) } }
                    .onFailure { it.printStackTrace() }
            }
        }
    }

    fun AppState.getDateIfLoaded(date: LocalDate) = loadedDates[date]
}
