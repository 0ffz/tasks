package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun DayList(
    date: LocalDate,
    isToday: Boolean,
    height: Int,
) {
    Column(Modifier.fillMaxHeight().fillMaxWidth()) {
        DayTitle(date, isToday)
        Column(Modifier.padding(16.dp)) {
            repeat(height) {
                Timeslot {
                    var taskName by remember { mutableStateOf("Tast $it") }
                    var highlight by remember { mutableStateOf(Highlights.Unmarked) }
                    val highlightAnimate by animateColorAsState(highlight.color)
                    Task(
                        taskName,
                        onNameChange = { taskName = it },
                        highlight = highlightAnimate,
                        onTab = {
                            highlight = Highlights.entries[(highlight.ordinal + 1) % Highlights.entries.size]
                        },
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

enum class Highlights(
    val color: Color
) {
    Unmarked(Color.Transparent),
    Important(Color.Red.copy(alpha = 0.5f)),
    InProgress(Color.Yellow.copy(alpha = 0.5f)),
//    Done(Color.Green.copy(alpha = 0.5f)),
}

@Composable
fun DayTitle(date: LocalDate, isToday: Boolean) {
    val color =
        if (isToday) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        Modifier.padding(12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}",
            Modifier.weight(1f, true),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            date.dayOfWeek.name.lowercase().capitalize().take(3),
            style = MaterialTheme.typography.headlineSmall,
            color = color.copy(alpha = 0.6f)
        )
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = color
    )
}


fun Int.toTwoDigitString(): String {
    return if (this < 10) "0$this" else this.toString()
}
