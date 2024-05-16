package me.dvyy.tasks.ui.elements.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.sp
import me.dvyy.tasks.state.LocalAppState

@Composable
fun AppTopBarTitle() {
    // current week
    val app = LocalAppState
    val small by app.isSmallScreen.collectAsState()
    val weekStart by app.weekStart.collectAsState()
    val fontSize = if (small) 24.sp else 18.sp
    Text(
        "Week ${(weekStart.dayOfMonth / 7) + 1}, ${
            weekStart.month.name.lowercase().capitalize(Locale.current)
        } ${weekStart.year}",
        style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
    )
}
