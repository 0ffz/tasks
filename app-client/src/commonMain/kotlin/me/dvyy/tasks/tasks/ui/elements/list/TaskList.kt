package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.dataOrNull
import me.dvyy.tasks.core.ui.isOfType
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask
import me.dvyy.tasks.tasks.ui.elements.task.Task
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.utils.CachedUpdate
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.loadedOrNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    listId: ListId,
    tasks: Loadable<List<Uuid>>, //TODO represent loading state explicitly?
    properties: TaskListProperties,
    colored: Boolean = false,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskListInteractions,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    viewModel: TasksViewModel = viewModel(),
) {
    val ui = LocalUIState.current
    val listDropTarget = Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { it.isOfType<TaskId>() },
        target = remember(listId) {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    println("reordering $event")
                    reorderInteractions.onDragEnterColumn(listId, event.dataOrNull<TaskId>() ?: return false)
                    return true
                }
            }
        }
    )

    Column(
        modifier.padding(top = 6.dp).fillMaxWidth()
    ) {
        val isLoading = tasks is Loadable.Loading
        TaskListTitle(
            Loadable.Loaded(properties),
            colored,
            interactions,
            loading = isLoading,
            key = listId,
        )
        val tasks = tasks.loadedOrNull() ?: return@Column
//        println("Loading ${listId.date} with ${tasks.size}")
        val scrollState = rememberScrollState()
        if (scrollable) Modifier.verticalScroll(scrollState)
        else Modifier

        fun String.isGroupToggle() = startsWith("--") || startsWith("==")
        Column {
            val selectedTask by viewModel.selectedTask.collectAsState()
//            val groupedTasks = mutableListOf(mutableListOf<TaskWithIDState>())
//            tasks.forEach { task ->
//                if (task.state.text.isGroupToggle()) groupedTasks.add(mutableListOf(task))
//                else groupedTasks.lastOrNull()?.add(task)
//            }

            Column(Modifier.padding(horizontal = 6.dp)) {
//                groupedTasks.forEachIndexed { groupIndex, tasksInGroup ->
//                    var isGroupHidden by remember { mutableStateOf(tasksInGroup.firstOrNull()?.state?.completed == true) }
                for ((index, taskId) in tasks.withIndex()) key(taskId) {
                    val selected = selectedTask?.task == taskId
                    val focusRequested = selected// && selectedTask?.requestFocus == true
                    val taskState = viewModel.watchTask(taskId).collectAsState(null).value
                    if (taskState == null) {
                        Box(Modifier.height(ui.tasks.height).fillMaxWidth())
                        continue
                    }
//                        val onChange = remember(task) { getInteractions(task) }::onTaskChanged
                    // cached task is the SSOT in this context, some things like text updates take too long to update in db
                    CachedUpdate(
                        key = taskId,
                        value = taskState,
                        onValueChanged = { viewModel.mutateTask(taskId, it) }
                    ) { cachedTask, setTask ->
                        val focusManager = LocalFocusManager.current
                        val keyboardOpen by keyboardAsState()
                        val isGroupToggle = index == 0 && cachedTask.text.isGroupToggle()

//                                LaunchedEffect(cachedTask) {
//                                    if (isGroupToggle) isGroupHidden = cachedTask.completed
//                                }

                        LaunchedEffect(keyboardOpen) {
                            if (!keyboardOpen) {
                                focusManager.clearFocus()
                            }
                        }

                        val taskInteractions = remember(cachedTask) {
                            viewModel.interactionsFor(listId.uuid, taskId)// TODO, listId, cachedTask, setTask)
                        }

//                                AnimatedVisibility(isGroupToggle || !isGroupHidden) {
                        Column {
                            ReorderableTask(key = taskId, reorderInteractions = reorderInteractions) {
                                Task(
                                    TaskUiState.fromModel(cachedTask),
                                    {
                                        setTask(
                                            Task(
                                                text = it.text,
                                                done = it.completed,
                                                highlight = it.highlight,
                                                parent = listId.uuid
                                            )
                                        )
                                    },
                                    selected,
                                    taskInteractions,
                                    focusRequested = focusRequested,
                                    forceShowCheckbox = isGroupToggle,
                                    overrideCheckboxIcon = if (isGroupToggle) AppIcons.ArrowDropDown else null,
                                    overrideCheckboxCompletedIcon = if (isGroupToggle) AppIcons.ArrowDropUp else null,
                                )
                            }
                            if (!isGroupToggle) HorizontalDivider()
                        }
                    }
                }

                Column(Modifier.clickableWithoutRipple {
                    interactions.createNewTask(true)
                }.then(listDropTarget)) {
                    Spacer(modifier = Modifier.height(ui.tasks.height))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
            if (scrollable && !ui.isSmall)
                Box(Modifier.fillMaxSize().then(listDropTarget))
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
