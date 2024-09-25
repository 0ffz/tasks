package me.dvyy.tasks.app.ui.elements

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.di.koinViewModel

@Composable
fun AppTopBarTitle(
    color: Color = Color.Unspecified,
    time: TimeViewModel = koinViewModel(),
) {
    // current week
    val weekStart by time.weekStart.collectAsState()
    Text(
        "Week ${(weekStart.dayOfMonth / 7) + 1}, ${
            weekStart.month.name.lowercase().capitalize(Locale.current)
        } ${weekStart.year}",
//        style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
        fontWeight = FontWeight.Medium,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}
