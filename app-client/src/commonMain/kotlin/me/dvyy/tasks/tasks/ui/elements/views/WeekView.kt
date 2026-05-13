package me.dvyy.tasks.tasks.ui.elements.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.elements.helpers.NonlazyGrid
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun BoxWithConstraintsScope.WeekView(
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    startAtToday: Boolean = false,
    takeDays: Int = 7,
    isSmall: Boolean,
) {
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
