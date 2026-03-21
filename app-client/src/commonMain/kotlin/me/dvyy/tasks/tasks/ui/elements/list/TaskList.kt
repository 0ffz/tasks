package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import kotlinx.collections.immutable.ImmutableList
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask
import me.dvyy.tasks.tasks.ui.elements.task.Task
import me.dvyy.tasks.tasks.ui.state.ProjectState
import me.dvyy.tasks.utils.*

@Composable
fun Project(
    list: ListId,
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
    displayOptions: ProjectDisplayOptions = rememberProjectDisplayOptions(),
    state: ProjectState = tasksViewModel.rememberUpdatedProjectState(list),
) {
    val listDropTarget = Modifier.dropTarget(
        key = remember { list.uuid },
        state = LocalDragAndDropState.current,
        shouldStartDragAndDrop = { it.data is Dragged.Task },
        onDrop = {
            val task = (it.data as? Dragged.Task ?: return@dropTarget).uuid.asTask()
            UiLogger.v { "Reordering ${it.data}" }
            state.mutate.moveTask(task)
        }
    )
    Column(
        modifier.padding(top = 6.dp, start = 6.dp, end = 6.dp).fillMaxWidth()
    ) {
        // == Header
        ProjectHeader(state.header, list, displayOptions.coloredHeader, addTask = {
            state.mutate.addTask(atEnd = false)
        })

        // == Task list
        Tasks(list, state.children, lazyColumn = displayOptions.scrollable && displayOptions.fullHeight)

        // == Empty task slot for adding task below
        Column(Modifier.clickableWithoutRipple {
            state.mutate.addTask(atEnd = true)
        }.then(listDropTarget)) {
            Spacer(modifier = Modifier.height(UI.tasks.height))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        // == Drop target for rest of empty vertical space
        if (displayOptions.fullHeight) {
            Box(Modifier.fillMaxSize().then(listDropTarget))
        }
    }
}

@Composable
private fun Tasks(
    list: ListId,
    ids: ImmutableList<TaskId>,
    lazyColumn: Boolean = false,
) {
    //TODO double check what this does
    val focusManager = LocalFocusManager.current
    val keyboardOpen by keyboardAsState()
    LaunchedEffect(keyboardOpen) {
        if (!keyboardOpen) {
            focusManager.clearFocus()
        }
    }
    if (lazyColumn) LazyColumn {
//        Rebugger(mapOf("list" to list, "ids" to ids, "viewModel" to viewModel), composableName = "List ${list.uuid}")
        items(ids, key = { it.uuid }) { id ->
            TaskFromId(list, id)
            HorizontalDivider()

        }
    } else Column {
        for (id in ids) {
            key(id.uuid) {
                TaskFromId(list, id)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun TaskFromId(list: ListId, id: TaskId, viewModel: TasksViewModel = viewModel()) {
    val task = remember(list, id) { viewModel.watchTask(list, id) }.collectAsState().value
    if (task == null) {
        Box(Modifier.fillMaxWidth().height(UI.tasks.height)) {}
        return
    }
//    Rebugger(mapOf("task" to task), composableName = "Task $id")
    ReorderableTask(
        enabled = !task.selected,
        key = id,
        onDropTask = { task.mutate.dropTaskOnThis(it) }
    ) {
        CachedUpdate(id, task.uiState, task.setTask) { uiState, update ->
            val caching = task.copy(uiState = uiState, setTask = { update(it) })
            Task(caching, focusRequested = task.selected)
        }
    }
}
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun TaskList(
//    listId: ListId,
//    tasks: Loadable<List<Uuid>>, //TODO represent loading state explicitly?
//    modifier: Modifier = Modifier,
//    scrollable: Boolean = false,
//    viewModel: TasksViewModel = viewModel(),
//) {
//    val ui = LocalUIState.current
//
//    Column(
//        modifier.padding(top = 6.dp).fillMaxWidth()
//    ) {
//        val isLoading = tasks is Loadable.Loading
//        val tasks = tasks.loadedOrNull() ?: return@Column
////        println("Loading ${listId.date} with ${tasks.size}")
//        val scrollState = rememberScrollState()
//        if (scrollable) Modifier.verticalScroll(scrollState)
//        else Modifier
//
//        fun String.isGroupToggle() = startsWith("--") || startsWith("==")
//        Column {
////            val selectedTask by viewModel.selectedTask.collectAsState()
//            Column(Modifier.padding(horizontal = 6.dp)) {
//                for ((index, taskId) in tasks.withIndex()) key(taskId) {
////                    val selected = selectedTask?.task == taskId
////                    val focusRequested = selected// && selectedTask?.requestFocus == true
//                    val taskState = viewModel.watchTask(taskId).collectAsState().value
//                    if (taskState == null) {
//                        Box(Modifier.height(ui.tasks.height).fillMaxWidth())
//                        continue
//                    }
//                    CachedUpdate(
//                        key = taskId,
//                        value = taskState,
//                        onValueChanged = { viewModel.mutateTask(taskId, it) }
//                    ) { cachedTask, setTask ->
////                        val isGroupToggle = index == 0 && cachedTask.text.isGroupToggle()
//
////                                LaunchedEffect(cachedTask) {
////                                    if (isGroupToggle) isGroupHidden = cachedTask.completed
////                                }
//
//                        val taskInteractions = remember(cachedTask) {
//                            viewModel.interactionsFor(listId.uuid, taskId)// TODO, listId, cachedTask, setTask)
//                        }
//
////                                AnimatedVisibility(isGroupToggle || !isGroupHidden) {
//                        Column {
////                                Task(
////                                    TaskUiState.fromModel(cachedTask),
////                                    {
////                                        setTask(
////                                            TaskModel(
////                                                text = it.text,
////                                                done = it.completed,
////                                                highlight = it.highlight,
////                                                parent = listId.uuid
////                                            )
////                                        )
////                                    },
////                                    selected,
////                                    taskInteractions,
////                                    focusRequested = focusRequested,
////                                    forceShowCheckbox = isGroupToggle,
////                                    overrideCheckboxIcon = if (isGroupToggle) AppIcons.ArrowDropDown else null,
////                                    overrideCheckboxCompletedIcon = if (isGroupToggle) AppIcons.ArrowDropUp else null,
////                                )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
