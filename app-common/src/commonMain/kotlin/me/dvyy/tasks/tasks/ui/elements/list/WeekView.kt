package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import me.dvyy.tasks.utils.Loadable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun WeekView(
    tasksViewModel: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    startAtToday: Boolean = false,
    takeDays: Int = 7,
) {
    val scrollState = rememberScrollState()
    Scaffold(snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }) {
//        TODO val reorderInteractions = tasksViewModel.reorderInteractions()
        val columns = if (UI.isSmall) 1 else takeDays
        val weekStart by (if (startAtToday) time.today else time.weekStart).collectAsState()
        val datesScrollable = Modifier.optional(UI.isSmall) { verticalScroll(scrollState) }
        val today by time.today.collectAsState()

        NonlazyGrid(
            columns = columns,
            itemCount = takeDays,
            modifier = Modifier.fillMaxSize().then(datesScrollable),
        ) { dayIndex ->
            val day = weekStart.plus(DatePeriod(days = dayIndex))
            val isToday = day == today
            val listId = ListId.forDate(day)
            val properties by tasksViewModel.getListProperties(listId).collectAsState()
            val tasks by remember(listId) { tasksViewModel.watchTasksFor(listId.uuid) }.collectAsState(listOf())
            println("Tasks are: $tasks")
            var scrollToPosition by remember { mutableStateOf(0F) }
            TaskList(
                listId = listId,
                tasks = Loadable.Loaded(tasks),
                properties = properties,
                colored = isToday,
                viewModel = tasksViewModel,
                reorderInteractions = TaskReorderInteractions(),//reorderInteractions,
                interactions = tasksViewModel.listInteractionsFor(listId.uuid),//tasksViewModel.listInteractionsFor(listId),
                scrollable = !UI.isSmall,
                modifier = Modifier.onGloballyPositioned { coords ->
                    scrollToPosition = coords.positionInRoot().y
                }
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
