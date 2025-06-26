package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.syncengine.db.Database
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.tasks.ui.elements.list.TaskListInteractions
import kotlin.uuid.Uuid

data class TaskWithList(
    val list: Uuid,
    val task: Uuid,
)

class TasksViewModel(
    val db: AppDatabase,
) : ViewModel() {
    val selectedTask = MutableStateFlow<TaskWithList?>(null)
//    val projects = MutableStateFlow<>()

    fun watchTasksFor(list: Uuid): Flow<List<Uuid>> = Database.watch(NotesTable) {
        db.tasks.childrenOf(list)
    }

    fun watchTask(id: Uuid) = Database.watch(NotesTable) {
        db.tasks.get(id)
    }

    fun mutateTask(id: Uuid, new: Task) = viewModelScope.launch {
        db.mutateTasks.patch(id, new)
//        val task = Database.read {
//            db.tasks.get(id)
//        }
//        db.mutate(JsonPatchMutator(NotesTable.name, id, jsonSubtract(Task.serializer(), new, task)))
    }
//
//    fun deleteTask(id: Uuid) = viewModelScope.launch {
//        db.mutate(DeleteRowMutator(NotesTable.name, id))
//    }

    fun selectNextTask() = viewModelScope.launch {
        val curr = selectedTask.value ?: return@launch
        val next = Database.read { db.rank.getAfter(curr.task) }
        if (next != null) selectedTask.emit(curr.copy(task = next))
//        else createTask(curr.list, TODO())
    }

    fun selectTask(task: TaskWithList?) = selectedTask.update { task }

    fun deleteProject(id: Uuid) {
        TODO()
    }

    @Stable
    fun interactionsFor(list: Uuid, task: Uuid) = object : TaskInteractions {
        override fun onDelete() {
            viewModelScope.launch {
                db.mutateTasks.delete(task)
            }
        }

        override fun onSelect() = selectedTask.update { TaskWithList(list, task) }
    }

    @Stable
    fun listInteractionsFor(list: Uuid) = TaskListInteractions(
        createNewTask = {
            viewModelScope.launch {
                db.mutateTasks.create(Task("", false, list))
            }
        }
    )

    fun createProject() {
        TODO("Not yet implemented")
    }

    fun getListProperties(key: ListId): StateFlow<TaskListProperties> {
        return MutableStateFlow(TaskListProperties(displayName = "Temp"))
    }
}
