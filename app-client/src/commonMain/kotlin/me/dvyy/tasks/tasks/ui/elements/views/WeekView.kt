package me.dvyy.tasks.tasks.ui.elements.views

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.elements.helpers.NonlazyGrid
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeekView(
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    startAtToday: Boolean = false,
    takeDays: Int = 7,
) {
    Scaffold(snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
        val columns = if (UI.isSmall) 1 else takeDays
        val weekStart by (if (startAtToday) time.today else time.weekStart).collectAsState()

        val scrollState = rememberScrollState()
//        val datesScrollable = Modifier.
//            /*.optional(UI.isSmall) { */verticalScroll(
//            scrollState,
////            flingBehavior = rememberSnapFlingBehavior(scrollState)
//        )/* }*/
        val today by time.today.collectAsState()

        NonlazyGrid(
            columns = columns,
            itemCount = takeDays,
            modifier = Modifier
//                .fillMaxSize()
                .verticalScroll(scrollState)
//                .then(datesScrollable)
//                .padding(it),
        ) { dayIndex ->
            val day = weekStart.plus(DatePeriod(days = dayIndex))
            val isToday = day == today
            val listId = ListId.forDate(day)

            Project(
                listId,
                displayOptions = rememberProjectDisplayOptions(coloredHeader = isToday),
//                modifier = Modifier.onGloballyPositioned { coords -> scrollToPosition = coords.positionInRoot().y }
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
