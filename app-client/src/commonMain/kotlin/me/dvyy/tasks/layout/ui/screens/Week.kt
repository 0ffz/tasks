package me.dvyy.tasks.layout.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Calendar
import dev.seyfarth.tablericons.outlined.CalendarEvent
import dev.seyfarth.tablericons.outlined.CalendarMonth
import dev.seyfarth.tablericons.outlined.CalendarWeek
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.ChevronRight
import kotlinx.coroutines.delay
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.layout.ui.screens.builder.screen
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.elements.helpers.NonlazyGrid
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import me.dvyy.tasks.time.TimeViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel
import kotlin.time.Duration.Companion.seconds


fun weekScreen(screen: ScreenDest.Week) = screen(
    icon = when (screen.takeDays) {
        7 -> TablerIcons.Outlined.CalendarWeek
        3 -> TablerIcons.Outlined.CalendarMonth
        else -> TablerIcons.Outlined.Calendar
    },
    leadingInfo = {
        Text("23 tasks this week", maxLines = 1, overflow = TextOverflow.Ellipsis)
    },
    tabLabel = {
        val text = when (screen.takeDays) {
            7 -> "Week view"
            3 -> "3-day view"
            else -> "Today"
        }
        AnnotatedString(text)
    },
    trailingOptions = { WeekActions() },
) { BoxWithConstraints { WeekScreen(screen.startAtToday, screen.takeDays, isSmall = maxWidth < 600.dp) } }

@Composable
fun WeekActions() {
    val time: TimeViewModel by rememberViewModel()
//    BoxButton(AppIcons.Filter, onClick = { time.goToThisWeek() }, tooltip = "Filter")
    BoxButton(AppIcons.CalendarEvent, onClick = { time.goToThisWeek() }, tooltip = "Today")
    BoxButton(AppIcons.ChevronLeft, onClick = { time.goToPreviousWeek() }, tooltip = "Previous week")
    BoxButton(AppIcons.ChevronRight, onClick = { time.goToNextWeek() }, tooltip = "Next week")
}

@Composable
private fun BoxWithConstraintsScope.WeekScreen(
    startAtToday: Boolean = false,
    takeDays: Int = 7,
    isSmall: Boolean,
) {
    val time: TimeViewModel by rememberViewModel()
    val columns = if (isSmall) 1 else takeDays
    val weekStart by (if (startAtToday) time.today else time.weekStart).collectAsState()

    val scrollState = rememberScrollState()
    val today by time.today.collectAsState()

    //    LaunchedEffect(scrollToPosition) {
//        delay(1.seconds)
//        if(!scrolled) {
//            scrolled = true
//            scrollState.scrollTo(scrollToPosition.toInt())
//            Logger.i { "Scrolling to $scrollToPosition" }
//        }
//    }
    Box(Modifier.height(maxHeight)) {
        NonlazyGrid(
            columns = columns,
            itemCount = takeDays,
            modifier = Modifier.verticalScroll(scrollState).height(IntrinsicSize.Max)
        ) { dayIndex ->
            val day = weekStart.plus(DatePeriod(days = dayIndex))
            val isToday = day == today
            val listId = ListId.forDate(day)
            val bringIntoViewRequester = remember { BringIntoViewRequester() }

            //TODO rework to just use a single lazycolumn and scroll to the element for today at startup
            LaunchedEffect(Unit) {
                delay(0.5.seconds)
                if (isToday) bringIntoViewRequester.bringIntoView()
            }
            Project(
                listId,
                displayOptions = rememberProjectDisplayOptions(
                    coloredHeader = isToday,
                    scrollable = false,
                    fullHeight = !isSmall,
                ),
                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)/* { coords ->
                    scrollToPosition = coords.positionInRoot().y
                }*/
            )

//            LaunchedEffect(Unit) {
//                if (isToday && columns == 1) snapshotFlow { scrollToPosition }
//                    .drop(1)
//                    .take(1)
//                    .collectLatest { scrollState.scrollTo(scrollToPosition.roundToInt()) }
//            }
        }
    }
}
