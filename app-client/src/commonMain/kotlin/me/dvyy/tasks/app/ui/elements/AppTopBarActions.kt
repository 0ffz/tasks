package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.CalendarEvent
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.ChevronRight
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.time.TimeViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun AppTopBarActions() = Row {
    PlatformSpecificTopBarActions()
}

@Composable
expect fun PlatformSpecificTopBarActions()


@Composable
expect fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit)

@Composable
fun WeekViewActions() {
    val time: TimeViewModel by rememberViewModel()
    BoxButton(onClick = { time.goToThisWeek() }) {
        Icon(TablerIcons.Outlined.CalendarEvent, contentDescription = "Today")
    }
    BoxButton(onClick = { time.goToPreviousWeek() }) {
        Icon(TablerIcons.Outlined.ChevronLeft, contentDescription = "Previous")
    }
    BoxButton(onClick = { time.goToNextWeek() }) {
        Icon(TablerIcons.Outlined.ChevronRight, contentDescription = "Next")
    }
}
