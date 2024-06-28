package me.dvyy.tasks.tasks.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.ui.state.Loadable
import me.dvyy.tasks.app.ui.state.loadedOrNull
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.Highlight
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
    val selectedTask = MutableStateFlow<TaskId?>(null)

    val projects = listRepo.observeProjects()
        .stateIn(viewModelScope, WhileUiSubscribed, emptyList())

    fun selectTask(uuid: TaskId?) {
        selectedTask.update { uuid }
    }

    // These flows will stop when coroutines aren't actively using them, they're safe to store in a map here
    private val listObservers = mutableStateMapOf<ListId, StateFlow<Loadable<List<TaskWithIDState>>>>()

    @Composable
    fun tasksFor(listId: ListId): StateFlow<Loadable<List<TaskWithIDState>>> = remember(listId) {
        listObservers.getOrPut(listId) {
            listRepo.observeTasksFor(listId)
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
    }

    @Composable
    fun getListProperties(key: ListId) = remember(key) {
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

    fun interactionsFor(taskId: TaskId, listId: ListId, uiState: TaskUiState): TaskInteractions =
        DefaultTaskInteractions(taskId, listId, uiState)

    private fun taskAfter(listId: ListId, taskId: TaskId): TaskId? {
        val list = listObservers[listId]?.value?.loadedOrNull() ?: return null
        return list.getOrNull(list.indexOfFirst { it.uuid == taskId } + 1)?.uuid
    }

    private fun taskBefore(listId: ListId, taskId: TaskId): TaskId? {
        val list = listObservers[listId]?.value?.loadedOrNull() ?: return null
        return list.getOrNull(list.indexOfFirst { it.uuid == taskId } - 1)?.uuid
    }

    fun onTaskChanged(key: TaskId, newState: TaskUiState) = viewModelScope.launch {
        taskRepo.update(key) {
            it.copy(
                text = newState.text,
                completed = newState.completed,
                highlight = newState.highlight
            )
        }
    }

    @Stable
    inner class DefaultTaskInteractions(
        private val taskId: TaskId,
        private val listId: ListId,
        private val uiState: TaskUiState,
    ) : TaskInteractions {
        override fun toString(): String {
            return "DefaultTaskInteractions(taskId=$taskId, listId=$listId, uiState=$uiState)"
        }

        private fun update(updater: (Task) -> Task) = viewModelScope.launch {
            taskRepo.update(taskId, updater)
        }

        private fun selectNextTaskOrNew() {
            val nextTask = taskAfter(listId, /*selectedTask.value ?: */taskId)
            if (nextTask != null) {
                selectTask(nextTask)
            } else if (uiState.text.isNotEmpty()) {
                viewModelScope.launch {
                    selectTask(taskRepo.create(listId).uuid)
                }
            }
        }

        override val keyboardActions = KeyboardActions(onNext = { selectNextTaskOrNew() })

        override fun onListChanged(date: LocalDate) {
            viewModelScope.launch { taskRepo.move(taskId, ListId.forDate(date)) }
        }

        override fun onDelete() {
            viewModelScope.launch { taskRepo.delete(taskId) }
        }

        override fun onKeyEvent(event: KeyEvent): Boolean {
            if (event.key == Key.Backspace) {
                if (uiState.text.isEmpty()) {
                    viewModelScope.launch {
                        selectTask(taskBefore(listId, taskId))
                        taskRepo.delete(taskId)
                    }
                }
                return false
            }
            if (event.type != KeyEventType.KeyDown) return false
            when {
                event.isCtrlPressed && event.key == Key.E -> {
                    update { it.copy(highlight = Highlight.entries[(it.highlight.ordinal + 1) % Highlight.entries.size]) }
                }

                event.key == Key.Escape -> {
                    selectTask(null)
                }

                else -> return false
            }
            return true
        }

        override fun onSelect() {
            if (selectedTask.value != taskId) selectTask(taskId)
        }
    }
}
