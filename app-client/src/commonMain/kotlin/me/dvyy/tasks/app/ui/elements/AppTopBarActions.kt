package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ArrowBack
import dev.seyfarth.tablericons.outlined.ArrowForward
import dev.seyfarth.tablericons.outlined.CalendarEvent
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
        Icon(TablerIcons.Outlined.CalendarEvent, contentDescription = "Today")
    }
    BoxButton(onClick = { time.goToPreviousWeek() }) {
        Icon(TablerIcons.Outlined.ArrowBack, contentDescription = "Previous")
    }
    BoxButton(onClick = { time.goToNextWeek() }) {
        Icon(TablerIcons.Outlined.ArrowForward, contentDescription = "Next")
    }
}
