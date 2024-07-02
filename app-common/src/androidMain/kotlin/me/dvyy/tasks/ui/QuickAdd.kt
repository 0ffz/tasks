package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.rememberAppUIState
import me.dvyy.tasks.app.ui.state.loaded
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.TaskListTitle
import me.dvyy.tasks.tasks.ui.elements.task.TaskHighlight
import me.dvyy.tasks.tasks.ui.elements.task.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.TaskTextField
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.koin.compose.KoinContext

@Composable
fun QuickAdd(
    exit: () -> Unit,
    scheduleSync: () -> Unit,
    tasks: TasksViewModel = koinViewModel(),
    time: TimeViewModel = koinViewModel(),
) = AppTheme {
    KoinContext {
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
            var selectedDate by remember { mutableStateOf(time.today) }
            val listId = ListId.forDate(selectedDate)
            val coroutineScope = rememberCoroutineScope()

            fun saveTask() {
                tasks.createTask(task, listId)
                scheduleSync()
                exit()
            }

            val interactions = remember {
                object : TaskInteractions {
                    override fun onListChanged(date: LocalDate) {
                        selectedDate = date
                    }

                    override val keyboardActions = KeyboardActions(onDone = { saveTask() })
                    override val keyboardOptions = KeyboardOptions(imeAction = Done)

                }
            }
            Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    Box {
                        TaskListTitle(
                            props = TaskListProperties(date = selectedDate).loaded(),
                            colored = false,
                            loading = false,
                            showDivider = false,
                            key = listId
                        )
                    }
                    Box(
                        modifier = Modifier.height(ui.taskHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        TaskHighlight(task.text, task.highlight)
                        TaskTextField(
                            task = task,
                            selected = true,
                            focusRequested = true,
                            setTask = { task = it },
                            interactions = interactions,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    TaskOptions(
                        task = task,
                        setTask = { task = it },
                        initialDate = selectedDate,
                        interactions = interactions,
                        submitAction = { saveTask() }
                    )
                }
            }
        }
    }
}
