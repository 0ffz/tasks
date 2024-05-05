package me.dvyy.tasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.dvyy.tasks.state.Constants
import me.dvyy.tasks.ui.elements.week.DayList

@Composable
fun HomeScreen() {
    WeekView()
}

@Composable
fun WeekView() {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val weekStart = today.minus(today.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    BoxWithConstraints {
        val columns = remember(constraints) { if (constraints.maxWidth < Constants.WEEK_VIEW_MIN_WIDTH) 1 else 6 }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            fun isToday(index: Int) = index == today.dayOfWeek.ordinal
            items(5) { index ->
                DayList(
                    weekStart.plus(DatePeriod(days = index)),
                    isToday = isToday(index),
                    height = 8,
                )
            }
            item {
                Column {
                    repeat(2) {
                        DayList(
                            weekStart.plus(DatePeriod(days = 5 + it)),
                            isToday = isToday(5 + it),
                            height = 4,
                        )
                    }
                }
            }
        }
    }
}
