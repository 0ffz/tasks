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
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.rememberAppUIState
import me.dvyy.tasks.app.ui.state.loaded
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.di.appModule
import me.dvyy.tasks.di.viewModelsModule
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.TaskListTitle
import me.dvyy.tasks.tasks.ui.elements.task.TaskHighlight
import me.dvyy.tasks.tasks.ui.elements.task.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.TaskTextField
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun QuickAdd(
    exit: () -> Unit,
    time: TimeViewModel = koinInject(),
) = AppTheme {
    KoinApplication(application = {
        modules(appModule(), viewModelsModule())
    }) {
        val tasks = koinInject<TasksViewModel>()
        val ui = rememberAppUIState()
        CompositionLocalProvider(LocalUIState provides ui) {
            var title by remember { mutableStateOf("") }
            var highlight by remember { mutableStateOf(Highlight.Unmarked) }
            var selectedDate by remember { mutableStateOf(time.today) }

            val interactions = remember {
                TaskInteractions(
                    onTitleChanged = { title = it },
                    onHighlightChanged = { highlight = it },
                    onListChanged = { selectedDate = it },
                    keyboardActions = KeyboardActions(onDone = { /* TODO save task*/ exit() }),
                    keyboardOptions = KeyboardOptions(imeAction = Done),
                )
            }
            Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    Box {
                        TaskListTitle(
                            props = TaskListProperties(date = selectedDate).loaded(),
                            colored = false,
                            loading = false,
                            showDivider = false
                        )
                    }
                    Box(
                        modifier = Modifier.height(ui.taskHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        TaskHighlight(title, highlight)
                        TaskTextField(
                            title = title,
                            completed = false,
                            selected = true,
                            interactions = interactions,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    TaskOptions(
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
