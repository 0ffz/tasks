package me.dvyy.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.rememberAppUIState
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

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
//            var task by remember {
//                mutableStateOf(
//                    TaskUiState(
//                        text = "",
//                        completed = false,
//                        highlight = Highlight.Unmarked
//                    )
//                )
//            }
//            val projects by tasks.projects.collectAsState()
//            var selectedDate: LocalDate? by remember { mutableStateOf(null) }
//            val listId =
//                if (selectedDate != null) ListId.forDate(selectedDate!!)
//                else projects.firstOrNull() ?: ListId.forDate(time.today.value)
//
//            fun saveTask() {
//                tasks.createTask(task, listId, atEndOfList = false)
//                scheduleSync()
//                exit()
//            }
//
//            val interactions = remember {
//                object : TaskInteractions {
//                    override fun onListChanged(date: LocalDate) {
//                        selectedDate = date
//                    }
//
//                    override val keyboardActions = KeyboardActions(onDone = { saveTask() })
//                    override val keyboardOptions = KeyboardOptions(imeAction = Done)
//
//                }
//            }
//            TaskSelectedSurface(
//                visible = true,
//                task.highlight,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(UI.padding.md)) {
//                    val listProps by tasks.getListProperties(listId).collectAsState()
//                    Box {
//                        TaskListTitle(
//                            props = listProps,
//                            colored = false,
//                            loading = false,
//                            showDivider = false,
//                            key = listId
//                        )
//                    }
//                    Box(
//                        modifier = Modifier.height(ui.tasks.height),
//                        contentAlignment = Alignment.CenterStart,
//                    ) {
//                        TaskTextField(
//                            task = task,
//                            selected = true,
//                            focusRequested = true,
//                            setTask = { task = it },
//                            interactions = interactions,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                    TaskOptions(
//                        task = task,
//                        setTask = { task = it },
//                        initialDate = selectedDate,
//                        interactions = interactions,
//                        submitAction = { saveTask() }
//                    )
//                }
//            }
        }
    }
}
