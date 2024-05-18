package me.dvyy.tasks.ui.elements.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.sp
import me.dvyy.tasks.state.LocalTimeState
import me.dvyy.tasks.state.LocalUIState

@Composable
fun AppTopBarTitle() {
    // current week
    val time = LocalTimeState.current
    val responsive = LocalUIState.current
    val weekStart by time.weekStart.collectAsState()
    val fontSize = if (responsive.smallTopBar) 18.sp else 20.sp
    Text(
        "Week ${(weekStart.dayOfMonth / 7) + 1}, ${
            weekStart.month.name.lowercase().capitalize(Locale.current)
        } ${weekStart.year}",
        style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
    )
}
