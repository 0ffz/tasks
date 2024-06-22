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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.state.Loadable
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskWithIDState
import me.dvyy.tasks.tasks.ui.state.TaskUiState

sealed interface SyncState {
    data object InProgress : SyncState
    data object UnSynced : SyncState
    data object Success : SyncState
    data object Error : SyncState
}

class TasksViewModel(
    private val taskRepo: TaskRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    val selectedTask = MutableStateFlow<TaskId?>(null)

    val projects = taskRepo.getProjects()

    val loadedLists = mapOf<ListId, Flow<List<TaskWithIDState>>>()

    fun selectTask(uuid: TaskId?) {
        selectedTask.value = uuid
    }

    @Composable
    fun tasksFor(listId: ListId): StateFlow<Loadable<List<TaskWithIDState>>> = remember(listId) {
        taskRepo
            .tasksFor(listId)
            .map { list ->
                Loadable.Loaded(list.map { model ->
                    TaskWithIDState(
                        TaskUiState.fromModel(model),
                        model.uuid,
                    )
                })
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Loadable.Loading())
    }


    private val reorderState = ReorderState<TaskId>()

    fun reorderInteractions() = TaskReorderInteractions(
        draggedState = reorderState,
        onDragEnterItem = { targetTask, dragged ->
            selectTask(null)
            viewModelScope.launch {
                taskRepo.reorderTask(from = dragged.data, to = targetTask)
            }
        },
        onDragEnterColumn = { targetList, dragged ->
            val id = dragged.data
            viewModelScope.launch { taskRepo.moveTask(id, targetList) }
        }
    )

    @Composable
    fun getListProperties(key: ListId) = remember(key) {
        flow {
            emitAll(taskRepo.getListProperties(key).map { Loadable.Loaded(it) })
        }.stateIn(viewModelScope, SharingStarted.Eagerly, Loadable.Loading())
    }

    fun createProject(name: String) = viewModelScope.launch {
        taskRepo.createList(ListId.newProject(), TaskListProperties(displayName = name))
    }

    fun listInteractionsFor(list: ListId) = TaskListInteractions(
        createNewTask = { viewModelScope.launch { selectTask(taskRepo.createTask(list).uuid) } },
        onTitleChange = { title ->
            viewModelScope.launch {
                taskRepo.upsertProject(list, TaskListProperties(displayName = title, date = list.date))
            }
        },
    )

    fun interactionsFor(taskId: TaskId): TaskInteractions {
        fun update(updater: (Task) -> Task) = viewModelScope.launch {
            taskRepo.updateTask(taskId, updater)
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
                viewModelScope.launch { taskRepo.moveTask(taskId, ListId.forDate(date)) }
            },
            onSelect = { selectTask(taskId) },
            onDelete = {

//                val previous = taskRepo.taskBefore(taskId)
//                selectTask(previous)
                viewModelScope.launch { taskRepo.deleteTask(taskId) }
            },
            onKeyEvent = { event, uiState ->
                //TODO backspace
                if (event.key == Key.Backspace) {
                    if (uiState.text.isEmpty()) {
//                        selectTask(taskRepo.taskBefore(taskId))
                        viewModelScope.launch { taskRepo.deleteTask(taskId) }
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
