package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.model.components.ProjectModel
import me.dvyy.tasks.model.components.TaskInList
import me.dvyy.tasks.model.components.TaskModel
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.NotesTable
import me.dvyy.tasks.model.database.actions.MoveTaskAction
import me.dvyy.tasks.tasks.ui.state.ProjectHeaderState
import me.dvyy.tasks.tasks.ui.state.ProjectMutations
import me.dvyy.tasks.tasks.ui.state.ProjectState
import me.dvyy.tasks.tasks.ui.state.TaskMutations
import me.dvyy.tasks.tasks.ui.state.TaskState
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import me.dvyy.tasks.utils.UiLogger
import me.dvyy.tasks.utils.combinedStateFlow
import me.dvyy.tasks.utils.defaults
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class TasksViewModel(
    val db: AppDatabase,
) : ViewModel() {
    val selectedTask = MutableStateFlow<TaskInList?>(null)

    val projects = db.watch(NotesTable.name) {
        projects.getAll()
    }

    fun watchList(list: ListId): StateFlow<ProjectState> {
        val mutations = projectMutations(list)

        return viewModelScope.combinedStateFlow(
            watchChildren(list.uuid) defaults listOf(),
            watchProjectTitle(list.uuid) defaults null,
        ) { children, model ->
            UiLogger.v { "Sending task list: $children" }
            val header = when {
                list.isDate -> ProjectHeaderState.Date(date = list.date!!)
                else -> ProjectHeaderState.Named(
                    displayName = model?.title ?: "Untitled",
                    onRename = { renameProject(list, it) }
                )
            }
            ProjectState(header, children.toImmutableList(), mutations)
        }
        /*return combine(
            watchChildren(list.uuid).distinctUntilChanged(),
            watchProjectTitle(list.uuid).distinctUntilChanged(),
        ) { children, model ->
            val header = when {
                list.isDate -> ProjectHeaderState.Date(date = list.date!!)
                else -> ProjectHeaderState.Named(
                    displayName = model?.title ?: "Untitled",
                    onRename = { renameProject(list, it) }
                )
            }
            ProjectState(header, children.toImmutableList(), mutations)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ProjectState(ProjectHeaderState.Loading, persistentListOf(), mutations)
        )*/
    }


    fun watchProjectTitle(list: Uuid): Flow<ProjectModel?> = db.watch(NotesTable.name) {
        projects.crud.get(list)
    }

    private fun watchChildren(list: Uuid) = db.watch(NotesTable.name) {
        rank.childrenOf(list).map { it.asTask() }
    }

    fun watchTask(list: ListId, id: TaskId): StateFlow<TaskState?> {
        val mutations = taskMutations(list, id)
//        return MutableStateFlow(null)
        return combine(
            selectedTask.map { it?.task == id.uuid }.distinctUntilChanged(),
            watchTaskUiState(id).distinctUntilChanged()
        ) { selected, ui ->
            UiLogger.v { "Sending task $id, state $ui" }
            if (ui == null) return@combine null
            TaskState(uiState = ui, selected = selected, setTask = { mutateTask(id, it) }, mutate = mutations)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    }

    private fun watchTaskUiState(id: TaskId) = db.watch(NotesTable.name) {
        val model = tasks.get(id.uuid) ?: return@watch null
        TaskUiState.fromModel(model)
    }

    private fun mutateTask(id: TaskId, new: TaskUiState) = viewModelScope.launch {
        db.mutate.tasks.update(id.uuid, new.toModel())
    }

    fun renameProject(id: ListId, newName: String) = viewModelScope.launch {
        db.mutate.projects.patch(id.uuid, ProjectModel(newName))
    }

    fun selectNextTaskOrNew() = viewModelScope.launch {
        val curr = selectedTask.value ?: return@launch
        val next = db.read { rank.getAfter(curr.task) }
        if (next != null) selectedTask.emit(curr.copy(task = next))
        else createAndSelectNewTask(curr.list)
    }

    fun selectTask(task: TaskInList?) = selectedTask.update { task }

    fun deleteProject(id: Uuid) {
        TODO()
    }

    private fun projectMutations(list: ListId) = object : ProjectMutations {
        override fun addTask(atEnd: Boolean) {
            createAndSelectNewTask(list.uuid, atEnd)
        }

        override fun moveTask(dragged: TaskId) {
            db.launchMutate(MoveTaskAction(dragged, list))
        }
    }

    private fun taskMutations(list: ListId, task: TaskId) = object : TaskMutations {
        override fun moveTo(date: LocalDate) {
            viewModelScope.launch {
                db.mutate(MoveTaskAction(task, toList = ListId.forDate(date)))
            }
        }

        override fun dropTaskOnThis(other: TaskId) {
            viewModelScope.launch {
                db.mutate(MoveTaskAction(other, toList = list, toTask = task))
            }
        }

        override fun onDelete() {
            viewModelScope.launch {
                db.mutate.tasks.delete(task.uuid)
            }
        }

        override fun onKeyEvent(event: KeyEvent, uiState: TaskUiState): Boolean {
            if (event.type == KeyEventType.KeyUp) return false
            return when {
                event.key == Key.Enter -> {
                    selectNext(uiState)
                    true
                }

                event.key == Key.Escape -> {
                    selectTask(null)
                    true
                }

                else -> false
            }
        }

        override fun onSelect() = selectedTask.update { TaskInList(list.uuid, task.uuid) }

        override fun selectNext(uiState: TaskUiState) {
            if (uiState.text.isNotEmpty()) selectNextTaskOrNew()
        }
    }

    fun createAndSelectNewTask(list: Uuid, atEnd: Boolean = true) = viewModelScope.launch {
//        val isLastEmpty = db.read {
//            val task =
//                (if (atEnd) rank.getLastTaskInList(list) else rank.getFirstTaskInList(list)) ?: return@read false
//            tasks.get(task)?.text?.isEmpty() == true
//        }
//        if (!isLastEmpty) {
        db.mutate.tasks.create(TaskModel(text = "", done = false, parent = list), atEnd = atEnd)
        delay(0.03.seconds)
//        }
        db.read {
            val task = (if (atEnd) rank.getLastTaskInList(list) else rank.getFirstTaskInList(list)) ?: return@read
            selectTask(TaskInList(list, task))
        }
    }

    fun createProject() = viewModelScope.launch {
        db.mutate.projects.create(ProjectModel("New Project"))
    }

    //TODO Is this breaking any compose practices? These could technically be emitted as flows but
    // that would mean reimplementing CachedUpdate for it, look around online.
    @Composable
    fun rememberUpdatedProjectState(id: ListId): ProjectState {
        val state by remember(id) { watchList(id) }.collectAsState()
        return state //TODO cached update for header rename
    }

    @Composable
    fun rememberUpdatedTaskState(list: ListId, id: TaskId): TaskState? {
        val state = remember(list, id) { watchTask(list, id) }.collectAsState().value ?: return null
        return state
    }
}
