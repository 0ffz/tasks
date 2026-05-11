package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.rememberAppUIState
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.ProjectHeader
import me.dvyy.tasks.tasks.ui.elements.task.TaskSelectedSurface
import me.dvyy.tasks.tasks.ui.elements.task.properties.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskTextField
import me.dvyy.tasks.tasks.ui.state.ProjectHeaderState
import me.dvyy.tasks.tasks.ui.state.TaskMutations
import me.dvyy.tasks.tasks.ui.state.TaskState
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuickAdd(
    exit: () -> Unit,
    scheduleSync: () -> Unit,
    tasks: TasksViewModel = koinViewModel(),
    time: TimeViewModel = koinViewModel(),
) = AppTheme {
    val ui = rememberAppUIState()
    CompositionLocalProvider(LocalUIState provides ui) {
        var task by remember {
            mutableStateOf(
                TaskUiState(
                    text = "",
                    completed = false,
                    highlight = Highlight.Unmarked
                )
            )
        }
        var selectedDate: LocalDate? by remember { mutableStateOf(null) }
        val mutations = remember {
            object : TaskMutations {
                override fun moveTo(date: LocalDate) {
                    selectedDate = date
                }
            }
        }
        val state = TaskState(task, selected = true, date = selectedDate, setTask = {
            task = it
        }, mutate = mutations)
        val projects by tasks.projects.collectAsState()
        val firstProject = projects.firstOrNull()
        val today = time.today.collectAsState().value
        val listId =
            if (selectedDate != null) ListId.forDate(selectedDate!!)
            else firstProject?.id?.asList() ?: ListId.forDate(today)

        //
        val scope = rememberCoroutineScope()
        fun saveTask() {
            scope.launch {
                tasks.createAndSelectNewTask(listId.uuid, atEnd = false, state = task).join()
                scheduleSync()
                exit()
            }
        }

        TaskSelectedSurface(
            visible = true,
            task.highlight,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(UI.padding.md)) {
                val header = if (selectedDate != null || firstProject == null)
                    ProjectHeaderState.Date(selectedDate ?: today)
                else ProjectHeaderState.Named(firstProject.title, onRename = { }, canRename = false)
                ProjectHeader(header, listId, colored = true, addTask = null)

                Box(
                    modifier = Modifier.height(ui.tasks.height),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    TaskTextField(
                        task = state,
                        focusRequested = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TaskOptions(
                    task = state,
                    submitAction = { saveTask() }
                )
            }
        }
    }
}
