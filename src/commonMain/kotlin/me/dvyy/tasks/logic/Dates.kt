package me.dvyy.tasks.logic

import kotlinx.datetime.LocalDate
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.DateState
import me.dvyy.tasks.state.TaskState

object Dates {
    fun AppState.loadDate(date: LocalDate): DateState {
        return loadedDates.getOrPut(date) { DateState(date) }
    }
}
