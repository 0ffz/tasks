package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun DayList(
    date: LocalDate,
) {
    Column(Modifier.fillMaxHeight().width(400.dp)) {
        DayTitle(date)
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Column(Modifier.padding(16.dp)) {
            repeat(8) {

                Timeslot {
                    Task("Task $it")
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DayTitle(date: LocalDate) {
    Row(
        Modifier.padding(12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            "${date.dayOfMonth.toTwoDigitString()}.${date.monthNumber.toTwoDigitString()}",
            Modifier.weight(1f, true),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            date.dayOfWeek.name.lowercase().capitalize(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
        )
    }
}


fun Int.toTwoDigitString(): String {
    return if (this < 10) "0$this" else this.toString()
}
