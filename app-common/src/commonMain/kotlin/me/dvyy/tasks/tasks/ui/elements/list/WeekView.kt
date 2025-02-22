package me.dvyy.tasks.tasks.ui.elements.list

import TasksViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.WeekViewActions
import me.dvyy.tasks.core.ui.MultiplatformDragAndDropData
import me.dvyy.tasks.core.ui.platformDragAndDropSource
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.notes.ui.PageTopBar
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

val LocalLayoutActions = compositionLocalOf<LayoutActions> { error("No local layout structure") }

interface LayoutActions {
    fun close()

    val structure: LayoutStructure
}

@Composable
fun AppScreen(
    text: @Composable () -> Unit = {},
    topButtons: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val actions = LocalLayoutActions.current
    Column {
        PageTopBar(
            text = text,
            buttons = {
                topButtons()
                IconButton(onClick = { actions.close() }) {
                    Icon(AppIcons.Close, "Close split tab")
                }
            },
            modifier = Modifier.platformDragAndDropSource {
                actions.close()
                MultiplatformDragAndDropData(actions.structure, it)
            }
        )
        content()
    }
}

@Composable
fun WeekView(
    title: String = "Week view",
    tasksViewModel: TasksViewModel = koinViewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    startAtToday: Boolean = false,
    takeDays: Int = 7,
) {
    val weekStart by (if (startAtToday) time.today else time.weekStart).collectAsState()
    val scrollState = rememberScrollState()
    AppScreen(text = { Text(title) }, topButtons = { if (takeDays == 7) WeekViewActions(time) }) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
            val columns = if (UI.isSmall) 1 else takeDays
            val datesScrollable = Modifier.optional(UI.isSmall) { verticalScroll(scrollState) }
            val today by time.today.collectAsState()

            NonlazyGrid(
                columns = columns,
                itemCount = takeDays,
                modifier = Modifier.fillMaxSize().then(datesScrollable),
            ) { dayIndex ->
                val day = weekStart.plus(DatePeriod(days = dayIndex))
                val path = tasksViewModel.vaultPathFor(day)
                val isToday = day == today
//            val properties by tasksViewModel.getListProperties(listId).collectAsState()
//            val tasks by tasksViewModel.tasksFor(tasksViewModel.vaultPathFor(day)).collectAsState()
                var scrollToPosition by remember { mutableStateOf(0F) }
                Project(
                    path = path,
//                tasks = tasks,
//                properties = properties,
//                colored = isToday,
//                viewModel = tasksViewModel,
//                interactions = tasksViewModel.listInteractionsFor(listId),
                    scrollable = !UI.isSmall,
                    showTitle = true,
                    properties = TaskListProperties(date = day),
//                modifier = Modifier.onGloballyPositioned { coords ->
//                    scrollToPosition = coords.positionInRoot().y
//                }
                )
                LaunchedEffect(Unit) {
                    if (isToday && columns == 1) snapshotFlow { scrollToPosition }
                        .drop(1)
                        .take(1)
                        .collectLatest { scrollState.scrollTo(scrollToPosition.roundToInt()) }
                }
            }
        }
    }
}
