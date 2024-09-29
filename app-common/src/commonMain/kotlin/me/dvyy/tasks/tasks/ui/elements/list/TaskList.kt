package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.AnimatedVisibility
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
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.CachedUpdate
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.task.ReorderableTask
import me.dvyy.tasks.tasks.ui.elements.task.Task
import me.dvyy.tasks.tasks.ui.elements.task.color
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.loadedOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    listId: ListId,
    tasks: Loadable<List<TaskWithIDState>>, //TODO represent loading state explicitly?
    properties: Loadable<TaskListProperties>,
    colored: Boolean = false,
    reorderInteractions: TaskReorderInteractions,
    interactions: TaskListInteractions,
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
) {
    val ui = LocalUIState.current
    val listDropTarget = Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { it.isOfType<TaskId>() },
        target = remember {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    return true
                }

                override fun onEntered(event: DragAndDropEvent) {
                    reorderInteractions.onDragEnterColumn(listId, event.dataOrNull<TaskId>() ?: return)
                }
            }
        }
    )

    Column(
        modifier.padding(top = 6.dp).fillMaxWidth()
    ) {
        val isLoading = tasks is Loadable.Loading
        TaskListTitle(
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

        // Split into two lists based on filter
//        val (shown, incompleteTasks) = tasks.partition { it.state.completed }

        fun String.isGroupToggle() = startsWith("--") || startsWith("==")
        Column(
            modifier = Modifier
        ) {
            val selectedTask by viewModel.selectedTask.collectAsState()
            val groupedTasks = mutableListOf(mutableListOf<TaskWithIDState>())
            tasks.forEach { task ->
                if (task.state.text.isGroupToggle()) groupedTasks.add(mutableListOf(task))
                else groupedTasks.lastOrNull()?.add(task)
            }
            Column(scrollModifier.padding(horizontal = 6.dp)) {
                groupedTasks.forEachIndexed { groupIndex, tasksInGroup ->
                    var isGroupHidden by remember { mutableStateOf(false) }
                    tasksInGroup.forEachIndexed { index, task ->
                        key(task.uuid) {
                            val selected = selectedTask?.taskId == task.uuid
                            val focusRequested = selected && selectedTask?.requestFocus == true
//                        val onChange = remember(task) { getInteractions(task) }::onTaskChanged
                            // cached task is the SSOT in this context, some things like text updates take too long to update in db
                            CachedUpdate(
                                key = task.uuid,
                                value = task.state,
                                onValueChanged = { viewModel.onTaskChanged(task.uuid, it) }
                            ) { cachedTask, setTask ->
                                val isGroupToggle = index == 0 && cachedTask.text.isGroupToggle()
                                LaunchedEffect(cachedTask) {
                                    if (isGroupToggle) isGroupHidden = cachedTask.completed
                                }
                                val focusManager = LocalFocusManager.current
                                val keyboardOpen by keyboardAsState()
                                LaunchedEffect(keyboardOpen) {
                                    if (!keyboardOpen) {
//                                    viewModel.selectTask(null)
                                        focusManager.clearFocus()
                                    }
                                }
                                val taskInteractions =
                                    remember(cachedTask) {
                                        viewModel.interactionsFor(task.uuid, listId, cachedTask, setTask)
                                    }

                                AnimatedVisibility(
                                    isGroupToggle || !isGroupHidden
                                ) {
                                    Column {
                                        ReorderableTask(key = task.uuid, reorderInteractions = reorderInteractions) {
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
                                }

                                if (isGroupToggle) {
                                    HorizontalDivider(
                                        thickness = 2.dp,
                                        color = cachedTask.highlight.color
                                            .takeIf { it != Color.Transparent }
                                            ?: MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                Column(Modifier.clickableWithoutRipple {
                    val lastTask = tasks.lastOrNull()
                    if (lastTask == null || lastTask.state.text.isNotEmpty())
                        interactions.createNewTask(true)
                    else viewModel.selectTask(lastTask.uuid, focus = true)
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
