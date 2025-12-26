package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.key.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.NotesTable
import me.dvyy.tasks.model.database.actions.MoveTaskAction
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

data class TaskWithList(
    val list: Uuid,
    val task: Uuid,
)

class TasksViewModel(
    val db: AppDatabase,
) : ViewModel() {
    val selectedTask = MutableStateFlow<TaskWithList?>(null)

    fun watchTasksFor(list: Uuid): Flow<List<Uuid>> = db.watch(NotesTable.name) {
        rank.childrenOf(list)
    }

    fun watchTask(id: Uuid) = db.watch(NotesTable.name) {
        tasks.get(id)
    }

    fun mutateTask(id: Uuid, new: Task) {
        viewModelScope.launch {
            db.mutate.tasks.update(id, new)
//        val task = Database.read {
//            db.tasks.get(id)
//        }
//        db.mutate(JsonPatchMutator(NotesTable.name, id, jsonSubtract(Task.serializer(), new, task)))
        }
    }
//
//    fun deleteTask(id: Uuid) = viewModelScope.launch {
//        db.mutate(DeleteRowMutator(NotesTable.name, id))
//    }

    fun selectNextTaskOrNew() = viewModelScope.launch {
        val curr = selectedTask.value ?: return@launch
        val next = db.read { rank.getAfter(curr.task) }
        if (next != null) selectedTask.emit(curr.copy(task = next))
        else createAndSelectNewTask(curr.list)
    }

    fun selectTask(task: TaskWithList?) = selectedTask.update { task }

    fun deleteProject(id: Uuid) {
        TODO()
    }

    @Stable
    fun interactionsFor(list: Uuid, task: Uuid) = object : TaskInteractions {
        override fun onListChanged(date: LocalDate) {
            viewModelScope.launch {
                db.mutate.tasks.move(task, ListId.forDate(date))
            }
        }
        override fun onDelete() {
            viewModelScope.launch {
                db.mutate.tasks.delete(task)
            }
        }

        override fun onKeyEvent(event: KeyEvent): Boolean {
            if (event.type == KeyEventType.KeyUp) return false
            return when {
                event.key == Key.Enter -> {
                    selectNextTaskOrNew()
                    true
                }

                else -> false
            }
        }

        override fun onSelect() = selectedTask.update { TaskWithList(list, task) }
    }

    @Stable
    fun reorderInteractions() = TaskReorderInteractions(
        onDragEnterItem = { target, dragged ->
            viewModelScope.launch {
                db.mutate(MoveTaskAction(dragged, toList = null, toTask = target))
            }
        },
        onDragEnterColumn = { list, dragged ->
            viewModelScope.launch {
                db.mutate(MoveTaskAction(dragged, list))
            }
        }
    )

    fun createAndSelectNewTask(list: Uuid, atEnd: Boolean = true) {
        viewModelScope.launch {
            val isLastEmpty = db.read {
                val task =
                    (if (atEnd) rank.getLastTaskInList(list) else rank.getFirstTaskInList(list)) ?: return@read false
                tasks.get(task)?.text?.isEmpty() == true
            }
            if (!isLastEmpty) {
                db.mutate.tasks.create(Task(text = "", done = false, parent = list), atEnd = atEnd)
                delay(0.03.seconds)
            }
            db.read {
                val task = (if (atEnd) rank.getLastTaskInList(list) else rank.getFirstTaskInList(list)) ?: return@read
                selectTask(TaskWithList(list, task))
            }
        }
    }

    @Stable
    fun listInteractionsFor(list: Uuid) = TaskListInteractions(
        createNewTask = { createAndSelectNewTask(list, atEnd = it) }
    )

    fun createProject() {
        TODO("Not yet implemented")
    }

    fun getListProperties(key: ListId): StateFlow<TaskListProperties> = when {
        key.isDate -> MutableStateFlow(TaskListProperties(date = key.date))
        else -> MutableStateFlow(TaskListProperties(displayName = "Temp"))
    }
}
