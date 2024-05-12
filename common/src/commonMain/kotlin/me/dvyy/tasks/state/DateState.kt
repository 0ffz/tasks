package me.dvyy.tasks.state

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

@Stable
data class DateState(val date: LocalDate) {
    val tasks = MutableStateFlow(listOf<TaskState>())
}
