package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.*

class TimeViewModel : ViewModel() {
    val timezone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timezone).date
    val weekStart = MutableStateFlow(today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY))

    fun nextWeek() {
        weekStart.value = weekStart.value.plus(1, DateTimeUnit.WEEK)
    }

    fun previousWeek() {
        weekStart.value = weekStart.value.minus(1, DateTimeUnit.WEEK)
    }
}
