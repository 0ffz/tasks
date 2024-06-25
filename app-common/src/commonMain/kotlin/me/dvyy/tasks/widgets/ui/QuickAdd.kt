package me.dvyy.tasks.widgets.ui

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
import me.dvyy.tasks.di.*
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskListTitle
import me.dvyy.tasks.tasks.ui.elements.task.TaskHighlight
import me.dvyy.tasks.tasks.ui.elements.task.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.TaskTextField
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication

@Composable
fun QuickAdd(
    exit: () -> Unit,
) = AppTheme {
    KoinIsolatedContext(context = koinApplication {
        modules(appModule(), syncModule(), repositoriesModule(), viewModelsModule())
    }) {
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
            val time: TimeViewModel = koinViewModel()
            var selectedDate by remember { mutableStateOf(time.today) }

            val interactions = remember {
                object : TaskInteractions {
                    override fun onTaskChanged(newState: TaskUiState) {
                        task = newState
                    }

                    override fun onListChanged(date: LocalDate) {
                        selectedDate = date
                    }

                    override val keyboardActions = KeyboardActions(onDone = { /* TODO save task*/ exit() })
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
                            key = ListId.forDate(selectedDate)
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
                        submitAction = {
                            // TODO save task
                            exit()
                        }
                    )
                }
            }
        }
    }
}
