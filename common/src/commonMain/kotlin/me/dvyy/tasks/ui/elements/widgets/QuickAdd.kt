package me.dvyy.tasks.ui.elements.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.launch
import me.dvyy.tasks.logic.Dates.getOrLoadDate
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.SyncStatus
import me.dvyy.tasks.state.AppConstants
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.task.TaskHighlight
import me.dvyy.tasks.ui.elements.task.TaskInteractions
import me.dvyy.tasks.ui.elements.task.TaskOptions
import me.dvyy.tasks.ui.elements.task.TaskTextField
import me.dvyy.tasks.ui.elements.week.DayTitle
import me.dvyy.tasks.ui.theme.AppTheme

@Composable
fun QuickAdd(exit: () -> Unit) = AppTheme {
    val state = remember { AppState() }
    CompositionLocalProvider(AppStateProvider provides state) {
        val newTask = remember {
            TaskState(uuid4(), "", state.today, SyncStatus.LOCAL_ONLY, false, Highlight.Unmarked).apply {
                focusRequested.value = true
            }
        }
        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(8.dp)) {
                Box {
                    val date by newTask.date.collectAsState()
                    DayTitle(date, isToday = false, loading = false, showDivider = false)
                }
                Box(
                    modifier = Modifier.height(AppConstants.taskHeight),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    TaskHighlight(newTask)
                    TaskTextField(
                        active = true,
                        completed = false,
                        newTask,
                        interactions = TaskInteractions(
                            onNameChange = { newTask.name.value = it }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                val scope = rememberCoroutineScope()
                TaskOptions(newTask, submitAction = {
                    scope.launch {
                        val loadedDate = state.getOrLoadDate(newTask.date.value)
                        loadedDate.tasks.value = loadedDate.tasks.value.plus(newTask)
                        state.saveDay(loadedDate)
                        exit()
                    }
                })
            }
        }
    }
}
