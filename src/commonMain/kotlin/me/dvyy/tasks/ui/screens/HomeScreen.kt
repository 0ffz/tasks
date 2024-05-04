package me.dvyy.tasks.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.dvyy.tasks.ui.elements.week.DayList

@Composable
fun HomeScreen() {
    DayList(Clock.System.now().toLocalDateTime(TimeZone.UTC).date)
}
