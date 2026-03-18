package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppTopBarActions() = Row {
    WeekViewActions()
    PlatformSpecificTopBarActions()
}

@Composable
expect fun PlatformSpecificTopBarActions()


@Composable
expect fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit)

@Composable
fun WeekViewActions(time: TimeViewModel = koinViewModel()) {
    IconButton(onClick = { time.goToThisWeek() }) {
        Icon(Icons.Outlined.Today, contentDescription = "Today")
    }
    IconButton(onClick = { time.goToPreviousWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
    }
    IconButton(onClick = { time.goToNextWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")
    }
}
