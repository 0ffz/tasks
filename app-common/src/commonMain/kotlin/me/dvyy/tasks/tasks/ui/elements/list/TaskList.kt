package me.dvyy.tasks.tasks.ui.elements.list

import TasksViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.dataOrNull
import me.dvyy.tasks.core.ui.isOfType
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.CachedUpdate
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask
import me.dvyy.tasks.tasks.ui.elements.task.Task
import me.dvyy.tasks.tasks.ui.elements.task.color
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.loadedOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    listId: VaultPath,
    tasks: Loadable<List<TaskUiStateWithPath>>, //TODO represent loading state explicitly?
    properties: Loadable<TaskListProperties>,
    colored: Boolean = false,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskListInteractions,
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    showTitle: Boolean = false,
) {
    val ui = LocalUIState.current
    val listDropTarget = Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { it.isOfType<VaultPath>() },
        target = remember(listId) {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    return true
                }

                override fun onEntered(event: DragAndDropEvent) {
                    reorderInteractions.onDragEnterColumn(listId, event.dataOrNull<VaultPath>() ?: return)
                }
            }
        }
    )

    Column(
        modifier.padding(top = 6.dp).fillMaxWidth()
    ) {
        val isLoading = tasks is Loadable.Loading
        if (showTitle) TaskListTitle(
            properties,
            colored,
            interactions,
            loading = isLoading,
            key = listId,
        )
        val tasks = tasks.loadedOrNull() ?: return@Column
        val scrollState = rememberScrollState()
        val scrollModifier =
            if (scrollable) Modifier.verticalScroll(scrollState)
            else Modifier

        fun String.isGroupToggle() = startsWith("--") || startsWith("==")
        Column {
//            val selectedTask by viewModel.selectedTask.collectAsState()
//            val groupedTasks = mutableListOf(mutableListOf<TaskWithIDState>())
//            tasks.forEach { task ->
//                if (task.state.text.isGroupToggle()) groupedTasks.add(mutableListOf(task))
//                else groupedTasks.lastOrNull()?.add(task)
//            }
            Column(scrollModifier.padding(horizontal = 6.dp)) {
//                groupedTasks.forEachIndexed { groupIndex, tasksInGroup ->
//                    var isGroupHidden by remember { mutableStateOf(tasksInGroup.firstOrNull()?.state?.completed == true) }
                tasks.forEachIndexed { index, (task, path) ->
                    key(path) {
                        val selected = false // TODO selectedTask?.taskId == task.uuid
                        val focusRequested = false // TODO selected && selectedTask?.requestFocus == true
//                        val onChange = remember(task) { getInteractions(task) }::onTaskChanged
                        // cached task is the SSOT in this context, some things like text updates take too long to update in db
                        CachedUpdate(
                            key = path,
                            value = task,
                            onValueChanged = { /*TODO viewModel.onTaskChanged(task.uuid, it)*/ }
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
                                viewModel.interactionsFor(path, listId, task)// TODO (task.uuid, listId, cachedTask, setTask)
                            }
//                                AnimatedVisibility(isGroupToggle || !isGroupHidden) {
                            Column {
                                ReorderableTask(key = path, reorderInteractions = reorderInteractions) {
                                    Task(
                                        cachedTask,
                                        setTask,
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
//                                }

                            if (isGroupToggle) HorizontalDivider(
                                thickness = 2.dp,
                                color = cachedTask.highlight.color
                                    .takeIf { it != Color.Transparent }
                                    ?: MaterialTheme.colorScheme.onSurface
                            )
//                            }
                        }
                    }
                }
                Column(Modifier.clickableWithoutRipple {
                    val lastTask = tasks.lastOrNull()
                    if (lastTask == null || lastTask.state.text.isNotEmpty())
                        interactions.createNewTask(true)
                    else {
                    }//TODO viewModel.selectTask(lastTask.uuid, focus = true)
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
