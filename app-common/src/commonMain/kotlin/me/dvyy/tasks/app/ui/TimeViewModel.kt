package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.minutes

class TimeViewModel : ViewModel() {
    val timezone = TimeZone.currentSystemDefault()
    private val _today = MutableStateFlow(getToday())
    val today = _today.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _today.update { getToday() }
                delay(1.minutes)
            }
        }
    }

    private val _weekStart =
        MutableStateFlow(today.value.minus(today.value.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY))
    val weekStart = _weekStart.asStateFlow()

    fun nextWeek() {
        _weekStart.update { it.plus(1, DateTimeUnit.WEEK) }
    }

    fun previousWeek() {
        _weekStart.update { it.minus(1, DateTimeUnit.WEEK) }
    }

    private fun getToday() = Clock.System.now().toLocalDateTime(timezone).date
}
