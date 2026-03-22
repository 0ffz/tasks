package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppTopBarActions() = Row {
    PlatformSpecificTopBarActions()
}

@Composable
expect fun PlatformSpecificTopBarActions()


@Composable
expect fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit)

@Composable
fun WeekViewActions(time: TimeViewModel = koinViewModel()) {
    BoxButton(onClick = { time.goToThisWeek() }) {
        Icon(Icons.Outlined.Today, contentDescription = "Today")
    }
    BoxButton(onClick = { time.goToPreviousWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
    }
    BoxButton(onClick = { time.goToNextWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")
    }
}
