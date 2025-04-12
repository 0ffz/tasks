import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Stable
import androidx.compose.ui.input.key.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.tasks.data.TasksLocalDataSource
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.TaskReorderInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import me.dvyy.tasks.tasks.ui.elements.list.TaskUiStateWithPath
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.WhileUiSubscribed

sealed interface SyncState {
    data object InProgress : SyncState
    data object UnSynced : SyncState
    data object Success : SyncState
    data object Error : SyncState
}

data class SelectedTask(
    val path: VaultPath,
    val requestFocus: Boolean,
)

class TasksViewModel(
    val taskDataSource: TasksLocalDataSource,
    val vault: Vault,
) : ViewModel() {
    val selectedTask = MutableStateFlow<SelectedTask?>(null)

    //    fun observeTags() = vault.queryAsFlow(
//
//    ).project(frontMatter("tags")).map {
//        it.flat
//    }
    //
//    val projects = listRepo.observeProjects()
//        .stateIn(viewModelScope, WhileUiSubscribed, emptyList())
//
    fun selectTask(path: VaultPath?, focus: Boolean = false) {
        selectedTask.update {
            if (path == null) null
            else SelectedTask(path, focus)
        }
    }

    //
//    // These flows will stop when coroutines aren't actively using them, they're safe to store in a map here
//    private val listTaskObservers = mutableStateMapOf<ListId, StateFlow<Loadable<List<TaskWithIDState>>>>()
//    private val listPropertiesObservers = mutableStateMapOf<ListId, StateFlow<Loadable<TaskListProperties>>>()
//
    fun tasksFor(path: VaultPath): StateFlow<Loadable<List<TaskUiStateWithPath>>> =
        taskDataSource.observeListTasks(path)
            .map { Loadable.Loaded(it) }
            .stateIn(viewModelScope, WhileUiSubscribed, Loadable.Loading())

    val dailyNotesFormat = LocalDate.Format {
        year();char('/');monthNumber();char('/');dayOfMonth()
    }

    fun vaultPathFor(date: LocalDate) = VaultPath(date.format(dailyNotesFormat))

    //    fun getListProperties(key: ListId) = listPropertiesObservers.getOrPut(key) {
//        listRepo.observeProperties(key)
//            .map { Loadable.Loaded(it) }
//            .stateIn(viewModelScope, WhileUiSubscribed, Loadable.Loading())
//    }
//
//
    fun reorderInteractions() = TaskReorderInteractions(
        onDragEnterItem = { targetTask, dragged ->
            selectTask(null)
//            viewModelScope.launch {
//                taskDataSource.moveTask(task = dragged, destId = targetTask)
//            }
        },
        onDragEnterColumn = { targetList, id ->
            viewModelScope.launch { taskDataSource.moveTask(id, targetList) }
        }
    )

    //
//    fun createProject(name: String? = null) = viewModelScope.launch {
//        listRepo.create(ListId.newProject(), TaskListProperties(displayName = name))
//    }
//
//    fun deleteProject(key: ListId) = viewModelScope.launch {
//        listRepo.delete(key)
//    }

    val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    val idLength = 8
    fun listInteractionsFor(listPath: VaultPath) = TaskListInteractions(
        createNewTask = { atEnd ->
            val randomId = buildString { repeat(idLength) { append(alphabet.random()) } }
            val taskName = "${listPath.displayName.take(16)}-$randomId.md"
            val childNotePath = vault.taskFolderFor(listPath).resolve(taskName)

            viewModelScope.launch {
                vault.createDocument(childNotePath) {
                    frontMatter {
                        projects = listOf(listPath.pathWithoutExt)
                        managed = true
                    }
                }/*, atEnd*/
                selectTask(childNotePath, focus = true)
            }
        },
        onPropertiesChanged = { props ->
            //TODO
//            viewModelScope.launch { vault.update(path) {
//            } }
        },
    )

    fun interactionsFor(
        task: VaultPath,
        parent: VaultPath,
//        taskId: TaskId,
//        listId: ListId,
        uiState: TaskUiState,
//        setUiState: (TaskUiState) -> Unit,
    ): TaskInteractions = DefaultTaskInteractions(task, parent, uiState)

    //
//    private fun taskAfter(listId: ListId, taskId: TaskId): TaskId? {
//        val list = listTaskObservers[listId]?.value?.loadedOrNull() ?: return null
//        return list.getOrNull(list.indexOfFirst { it.uuid == taskId } + 1)?.uuid
//    }
//
//    private fun taskBefore(listId: ListId, taskId: TaskId): TaskId? {
//        val list = listTaskObservers[listId]?.value?.loadedOrNull() ?: return null
//        return list.getOrNull(list.indexOfFirst { it.uuid == taskId } - 1)?.uuid
//    }
//
    fun onTaskChanged(key: VaultPath, newState: TaskUiState) = viewModelScope.launch {
        vault.update(key, clearOldFrontMatter = false) {
            frontMatter = newState.toFrontMatter()
            fileContent = newState.text
        }
    }

    //
//    fun createTask(task: TaskUiState, listId: ListId, atEndOfList: Boolean = true) = viewModelScope.launch {
//        val id = taskRepo.create(listId, atEndOfList).uuid
//        onTaskChanged(id, task)
//    }
//
//    fun bulkAdd(lines: List<String>) {
//        lines.forEach { line ->
//            val task = bulkAddRepo.parseLine(line)
//            createTask(TaskUiState.fromModel(task), task.list)
//        }
//    }
//
    @Stable
    inner class DefaultTaskInteractions(
        private val taskPath: VaultPath,
        private val listPath: VaultPath,
        private val uiState: TaskUiState,
//        private val setUiState: (TaskUiState) -> Unit,
    ) : TaskInteractions {
        override fun toString(): String {
            return "DefaultTaskInteractions(taskId=$taskPath, listId=$listPath, uiState=$uiState)"
        }

        private fun selectNextTaskOrNew() {
//            val nextTask = taskAfter(listId, /*selectedTask.value ?: */taskId)
//            if (nextTask != null) {
//                selectTask(nextTask, focus = true)
//            } else if (uiState.text.isNotEmpty()) {
//                viewModelScope.launch {
//                    selectTask(taskRepo.create(listId, atEndOfList = true).uuid, focus = true)
//                }
//            }
        }

        override val keyboardActions = KeyboardActions(onNext = {
            selectNextTaskOrNew()
        })

        override fun onListChanged(date: LocalDate) {
            viewModelScope.launch {
                taskDataSource.moveTask(taskPath, vaultPathFor(date))
            }
        }

        override fun onDelete() {
            viewModelScope.launch { vault.deleteDocument(taskPath) }
        }

        override fun onKeyEvent(event: KeyEvent): Boolean {
            if (event.type != KeyEventType.KeyDown) return false
            if (event.key == Key.Backspace) {
                if (uiState.text.isEmpty()) {
                    viewModelScope.launch {
//                        selectTask(taskBefore(listId, taskId), focus = true)
//                        taskRepo.delete(taskId)
                    }
                }
                return false
            }
//            fun color(index: Int) =
//                setUiState(uiState.copy(highlight = Highlight(Highlight.Type.entries[index], !event.isShiftPressed)))
            when {
//                event.isCtrlPressed -> {
//                    when (event.key) {
//                        Key.E -> {
//                            val shift = if (event.isShiftPressed) -1 else 1
//                            setUiState(uiState.copy(highlight = uiState.highlight.offsetBy(shift)))
//                        }
//
//                        Key.One -> color(1)
//                        Key.Two -> color(2)
//                        Key.Three -> color(3)
//                        Key.Four -> color(4)
//                        Key.Five -> color(5)
//                        Key.Six -> color(6)
//                        Key.Seven -> color(7)
//                        Key.Zero -> color(0)
//                        else -> return false
//                    }
//                }

                event.key == Key.Escape -> {
                    selectTask(null)
                }

                event.key == Key.Enter -> {
                    if (!event.isShiftPressed) selectNextTaskOrNew()
                }

                else -> return false
            }
            return true
        }

        override fun onSelect() {
            if (selectedTask.value?.path != taskPath) selectTask(taskPath)
        }
    }
}
