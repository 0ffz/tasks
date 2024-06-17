package me.dvyy.tasks.tasks.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import me.dvyy.tasks.app.ui.Task
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.Message
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.sync.ProjectNetworkModel
import me.dvyy.tasks.tasks.data.TaskRepository
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskWithIDState

sealed interface SyncState {
    data object InProgress : SyncState
    data object UnSynced : SyncState
    data object Success : SyncState
    data object Error : SyncState
}

class TasksViewModel(
    private val tasks: TaskRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    // Careful to update both task map and tasks per list, I'd like a SSOT but we really want both!
    val selectedTask = MutableStateFlow<Uuid?>(null)

    //    val requestedSelectTask = MutableStateFlow<Uuid?>(null)
    val projects = tasks.projects()

    fun selectTask(uuid: Uuid?) {
        selectedTask.value = uuid
    }

    sealed interface TaskList {
        data object Loading : TaskList

        @Immutable
        data class Data(val tasks: List<TaskWithIDState>) : TaskList
    }


    @Composable
    fun tasksFor(key: ListKey): StateFlow<TaskList> = remember(key) {
        tasks
            .tasksFor(key)
            .map { list ->
                TaskList.Data(list.map { model ->
                    TaskWithIDState(
                        //TODO maybe cache to avoid so many object recreations?
                        Task.fromModel(model, key),
                        model.uuid,
                    )
                })
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, TaskList.Loading)
    }


    private val reorderState = ReorderState<Uuid>()

    fun reorderInteractions() = TaskReorderInteractions(
        draggedState = reorderState,
        onDragEnterItem = { targetTask, dragged ->
            println(tasks.getModel(targetTask)?.name)
            selectTask(null)
            viewModelScope.launch {
                tasks.reorderTask(dragged.data, to = targetTask)
            }
        },
        onDragEnterColumn = { targetList, dragged ->
            val id = dragged.data
            viewModelScope.launch { tasks.moveTask(id, targetList) }
        }
    )

    @Composable
    fun getProject(key: ListKey.Project) = remember(key) {
        tasks.project(key)
    }

    fun createProject(name: String) = viewModelScope.launch {
        tasks.createProject(ListKey.Project(uuid4()), ListTitle.Project(name))
    }

    fun listInteractionsFor(key: ListKey) = TaskListInteractions(
        createNewTask = { viewModelScope.launch { selectTask(tasks.createTask(key)) } },
        onTitleChange = { title ->
            viewModelScope.launch {
                val uuid = (key as ListKey.Project).uuid //TODO use uuid for dates
                tasks.upsertProject(Message.Update(ProjectNetworkModel(key, title), uuid, Clock.System.now()))
            }
        },
    )

    fun interactionsFor(uuid: Uuid): TaskInteractions {
        fun update(updater: (TaskModel) -> TaskModel) = viewModelScope.launch { tasks.updateTask(uuid, updater) }
        return TaskInteractions(
            onTitleChanged = { name -> update { it.copy(name = name) } },
            onListChanged = { date ->
                viewModelScope.launch { tasks.moveTask(uuid, ListKey.Date(date)) }
            },
            onCheckChanged = { completed -> update { it.copy(completed = completed) } },
            onHighlightChanged = { highlight -> update { it.copy(highlight = highlight) } },
            onSelect = { selectTask(uuid) },
            onDelete = {
                val previous = tasks.taskBefore(uuid)
                selectTask(previous)
                viewModelScope.launch { tasks.deleteTask(uuid) }
            },
            onKeyEvent = { event ->
                if (event.key == Key.Backspace) {
                    val model = tasks.getModel(uuid) ?: return@TaskInteractions false
                    if (model.name.isEmpty()) {
                        selectTask(tasks.taskBefore(uuid))
                        viewModelScope.launch { tasks.deleteTask(uuid) }
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
                        selectNextTaskOrNew(uuid)
                        true
                    }

                    else -> false
                }
            },
            keyboardActions = KeyboardActions(onNext = { selectNextTaskOrNew(uuid) }),
        )
    }

    fun selectNextTaskOrNew(uuid: Uuid) {
        val nextTask = tasks.taskAfter(uuid)
        if (nextTask != null) {
            selectTask(nextTask)
        } else if (tasks.getModel(uuid)?.name?.isEmpty() != true) {
            viewModelScope.launch {
                val list = tasks.listKeyFor(uuid) ?: return@launch
                selectTask(tasks.createTask(list))
            }
        }
    }

    init {
        //TODO sync on startup
//        queueSync()
    }

    fun queueSync() = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        //TODO buffer
        _syncState.value = SyncState.InProgress
        runCatching {
            tasks.sync()
        }.onFailure {
            _syncState.value = SyncState.Error
            it.printStackTrace()
        }.onSuccess {
            _syncState.value = SyncState.Success
        }
    }
}
