package me.dvyy.tasks.tasks.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.state.Loadable
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.data.TaskListRepository
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskWithIDState
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.utils.WhileUiSubscribed

sealed interface SyncState {
    data object InProgress : SyncState
    data object UnSynced : SyncState
    data object Success : SyncState
    data object Error : SyncState
}

class TasksViewModel(
    private val taskRepo: TaskRepository,
    private val listRepo: TaskListRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    val selectedTask = MutableStateFlow<TaskId?>(null)

    val projects = listRepo.observeProjects()
        .stateIn(viewModelScope, WhileUiSubscribed, emptyList())

    fun selectTask(uuid: TaskId?) {
        selectedTask.value = uuid
    }

    @Composable
    fun tasksFor(listId: ListId): StateFlow<Loadable<List<TaskWithIDState>>> = remember(listId) {
        listRepo
            .observeTasksFor(listId)
            .map { list ->
                Loadable.Loaded(list.map { model ->
                    TaskWithIDState(
                        TaskUiState.fromModel(model),
                        model.uuid,
                    )
                })
            }
            .stateIn(viewModelScope, WhileUiSubscribed, Loadable.Loading())
    }

    @Composable
    fun getListProperties(key: ListId) = remember(key) {
        println("Ran for $key")
        listRepo.observeProperties(key)
            .map { Loadable.Loaded(it) }
            .stateIn(viewModelScope, WhileUiSubscribed, Loadable.Loading())
    }


    private val reorderState = ReorderState<TaskId>()

    fun reorderInteractions() = TaskReorderInteractions(
        draggedState = reorderState,
        onDragEnterItem = { targetTask, dragged ->
            selectTask(null)
            viewModelScope.launch {
                taskRepo.reorder(from = dragged.data, to = targetTask)
            }
        },
        onDragEnterColumn = { targetList, dragged ->
            val id = dragged.data
            viewModelScope.launch { taskRepo.move(id, targetList) }
        }
    )

    fun createProject(name: String? = null) = viewModelScope.launch {
        listRepo.create(ListId.newProject(), TaskListProperties(displayName = name))
    }

    fun listInteractionsFor(list: ListId) = TaskListInteractions(
        createNewTask = { viewModelScope.launch { selectTask(taskRepo.create(list).uuid) } },
        onPropertiesChanged = { props ->
            viewModelScope.launch { listRepo.update(list, props) }
        },
    )

    fun interactionsFor(taskId: TaskId): TaskInteractions {
        fun update(updater: (Task) -> Task) = viewModelScope.launch {
            taskRepo.update(taskId, updater)
        }
        return TaskInteractions(
            onTaskChanged = { uiState ->
                update {
                    it.copy(
                        text = uiState.text,
                        completed = uiState.completed,
                        highlight = uiState.highlight
                    )
                }
            },
            onListChanged = { date ->
                viewModelScope.launch { taskRepo.move(taskId, ListId.forDate(date)) }
            },
            onSelect = { selectTask(taskId) },
            onDelete = {

//                val previous = taskRepo.taskBefore(taskId)
//                selectTask(previous)
                viewModelScope.launch { taskRepo.delete(taskId) }
            },
            onKeyEvent = { event, uiState ->
                //TODO backspace
                if (event.key == Key.Backspace) {
                    if (uiState.text.isEmpty()) {
//                        selectTask(taskRepo.taskBefore(taskId))
                        viewModelScope.launch { taskRepo.delete(taskId) }
                    }
                    return@TaskInteractions false
                }
                if (event.type != KeyEventType.KeyDown) return@TaskInteractions false
                when {
//                    event.isCtrlPressed && event.key == Key.E -> {
//                        task.highlight.update {
//                            Highlight.entries[(it.ordinal + 1) % Highlight.entries.size]
//                        }
//                        true
//                    }

                    event.key == Key.Escape -> {
                        selectTask(null)
                        true
                    }

                    event.key == Key.Enter -> {
                        selectNextTaskOrNew(taskId)
                        true
                    }

                    else -> false
                }
            },
            keyboardActions = KeyboardActions(onNext = { selectNextTaskOrNew(taskId) }),
        )
    }

    fun selectNextTaskOrNew(uuid: TaskId) {
        // TODO
//        val nextTask = taskRepo.taskAfter(uuid)
//        if (nextTask != null) {
//            selectTask(nextTask)
//        } else if (taskRepo.getModel(uuid)?.text?.isEmpty() != true) {
//            viewModelScope.launch {
//                val list = taskRepo.listIdFor(uuid) ?: return@launch
//                selectTask(taskRepo.createTask(list))
//            }
//        }
    }

    fun queueSync() = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        //TODO buffer
        _syncState.value = SyncState.InProgress
        runCatching {
            taskRepo.sync()
        }.onFailure {
            _syncState.value = SyncState.Error
            it.printStackTrace()
        }.onSuccess {
            _syncState.value = SyncState.Success
        }
    }
}
