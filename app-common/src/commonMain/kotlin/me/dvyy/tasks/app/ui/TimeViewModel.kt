package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.*

class TimeViewModel : ViewModel() {
    val timezone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timezone).date
    private val _weekStart = MutableStateFlow(today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY))
    val weekStart = _weekStart.asStateFlow()

    fun nextWeek() {
        _weekStart.update { it.plus(1, DateTimeUnit.WEEK) }
    }

    fun previousWeek() {
        _weekStart.update { it.minus(1, DateTimeUnit.WEEK) }
    }
}
