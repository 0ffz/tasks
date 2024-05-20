package me.dvyy.tasks.ui.elements.widgets

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
import me.dvyy.tasks.di.appModule
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TimeState
import me.dvyy.tasks.state.rememberAppUIState
import me.dvyy.tasks.stateholder.TaskInteractions
import me.dvyy.tasks.ui.elements.task.TaskHighlight
import me.dvyy.tasks.ui.elements.task.TaskOptions
import me.dvyy.tasks.ui.elements.task.TaskTextField
import me.dvyy.tasks.ui.elements.week.TaskListKey
import me.dvyy.tasks.ui.elements.week.TaskListTitle
import me.dvyy.tasks.ui.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun QuickAdd(
    exit: () -> Unit,
    time: TimeState = koinInject(),
) = AppTheme {
    KoinApplication(application = {
        modules(appModule())
    }) {
        val ui = rememberAppUIState()
        CompositionLocalProvider(LocalUIState provides ui) {
            var title by remember { mutableStateOf("") }
            var highlight by remember { mutableStateOf(Highlight.Unmarked) }
            var listKey by remember { mutableStateOf(TaskListKey.Date(time.today)) }
            //TODO inject viewModel
//        val viewModel = viewModel { TasksViewModel(TaskRepository()) }
            val interactions = remember {
                TaskInteractions(
                    onTitleChanged = { title = it },
                    onHighlightChanged = { highlight = it },
                    onListChanged = { listKey = TaskListKey.Date(it) },
                    keyboardActions = KeyboardActions(onDone = { /* TODO save task*/ exit() }),
                    keyboardOptions = KeyboardOptions(imeAction = Done),
                )
            }
            Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    Box {
                        TaskListTitle(listKey, colored = false, loading = false, showDivider = false)
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
                        listKey = listKey,
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
