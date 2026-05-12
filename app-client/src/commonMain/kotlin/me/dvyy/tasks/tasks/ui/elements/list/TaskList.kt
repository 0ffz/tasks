package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask
import me.dvyy.tasks.tasks.ui.elements.task.Task
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskHighlight
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskTextField
import me.dvyy.tasks.tasks.ui.state.ProjectState
import me.dvyy.tasks.utils.CachedUpdate
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import me.dvyy.tasks.utils.UiLogger
import me.dvyy.tasks.utils.keyboardAsState

@Composable
fun Project(
    list: ListId,
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
    displayOptions: ProjectDisplayOptions = rememberProjectDisplayOptions(),
    state: ProjectState = tasksViewModel.rememberUpdatedProjectState(list),
) {
    val listDropTarget = Modifier.dropTarget(
        state = LocalDragAndDropState.current,
        shouldStartDragAndDrop = { it.data is Dragged.Task },
        onDrop = {
            UiLogger.v { "Reordering ${it.data}" }
            val task = (it.data as? Dragged.Task ?: return@dropTarget).uuid.asTask()
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

        val selected = tasksViewModel.selectedTask.collectAsState().value
        val index = if (selected?.list == list.uuid) {
            state.children.indexOf(selected.task.asTask())
        } else -1
        Tasks(list, state, lazyColumn = displayOptions.scrollable && displayOptions.fullHeight, selectedIndex = index, listDropTarget = listDropTarget)

        // == Drop target for rest of empty vertical space
        if (displayOptions.fullHeight) {
            Box(Modifier.fillMaxSize().then(listDropTarget))
        }
    }
}

@Composable
private fun Tasks(
    list: ListId,
    projectState: ProjectState,
    selectedIndex: Int,
    lazyColumn: Boolean = false,
    listDropTarget: Modifier,
) {
    val ids = projectState.children

    //TODO double check what this does
    val focusManager = LocalFocusManager.current
    val keyboardOpen by keyboardAsState()
    val state = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1) {
            val isVisible = state.layoutInfo.visibleItemsInfo.any { it.index == selectedIndex }
            if (!isVisible) state.animateScrollToItem(selectedIndex)
        }
    }
    LaunchedEffect(keyboardOpen) {
        if (!keyboardOpen) {
            focusManager.clearFocus()
        }
    }
    if (lazyColumn) LazyColumn(state = state) {
//        Rebugger(mapOf("list" to list, "ids" to ids, "viewModel" to viewModel), composableName = "List ${list.uuid}")
        items(ids, key = { it.uuid }) { id ->
            TaskFromId(list, id)
            HorizontalDivider()
        }
        item {
            // == Empty task slot for adding task below
            Column(Modifier.clickableWithoutRipple {
                projectState.mutate.addTask(atEnd = true)
            }.then(listDropTarget)) {
                Spacer(modifier = Modifier.height(UI.tasks.height))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    } else Column {
        for (id in ids) {
            key(id.uuid) {
                TaskFromId(list, id)
                HorizontalDivider()
            }
        }
        // == Empty task slot for adding task below
        Column(Modifier.clickableWithoutRipple {
            projectState.mutate.addTask(atEnd = true)
        }.then(listDropTarget)) {
            Spacer(modifier = Modifier.height(UI.tasks.height))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
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
        onDropTask = { task.mutate.dropTaskOnThis(it) },
        draggableContent = {
            Box(Modifier.widthIn(max = 400.dp), contentAlignment = Alignment.CenterStart) {
                TaskHighlight(task.uiState)
                TaskTextField(task)
            }
        }
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
