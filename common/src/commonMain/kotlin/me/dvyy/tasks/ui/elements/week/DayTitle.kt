package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun DayTitle(date: LocalDate, isToday: Boolean) {
    val color =
        if (isToday) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        Modifier.padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}",
            Modifier.weight(1f, true),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
        )
        Text(
            date.dayOfWeek.name.lowercase().capitalize().take(3),
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            color = color.copy(alpha = 0.6f)
        )
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = color
    )
}
