package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import me.dvyy.syncengine.db.Database
import me.dvyy.syncengine.db.tables.SubtaskRelation
import me.dvyy.tasks.database.Mutators
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.database.TasksView
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.mutators.DeleteRowMutator
import me.dvyy.tasks.model.mutators.JsonPatchMutator
import me.dvyy.tasks.model.schema.JsonTable
import me.dvyy.tasks.model.schema.NotesDAO
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.model.schema.RelationTableDAO
import kotlin.uuid.Uuid

data class TaskWithList(
    val list: Uuid,
    val task: Uuid,
)

class TasksViewModel(
    val tasks: NotesDAO<Task> = NotesDAO(Task.serializer(), NotesTable),
    val rank: RelationTableDAO<Task> = RelationTableDAO(SubtaskRelation),
    val mutators: Mutators,
) : ViewModel() {
    val selectedTask = MutableStateFlow<TaskWithList?>(null)
//    val projects = MutableStateFlow<>()

    fun watchTasksFor(list: Uuid): Flow<List<Uuid>> = Database.watch(SubtaskRelation) {
        rank.childrenOf(list)
    }
    fun watchTask(id: Uuid) = Database.watch(TasksView) {
        tasks.get(id)
    }

    fun mutateTask(id: Uuid, new: Task) = viewModelScope.launch {
        mutators(JsonPatchMutator(NotesTable.name, id, Json.encodeToJsonElement(new)))
    }

    fun deleteTask(id: Uuid) = viewModelScope.launch {
        mutators(DeleteRowMutator(NotesTable.name, id))
    }

    fun createTask(list: Uuid, task: Task) = viewModelScope.launch {
        TODO()
    }

    fun selectNextTask() = viewModelScope.launch {
        val curr = selectedTask.value ?: return@launch
        val next = Database.read { rank.getAfter(curr.task) }
        if (next != null) selectedTask.emit(curr.copy(task = next))
        else createTask(curr.list, TODO())
    }

    fun selectTask(task: TaskWithList?) = selectedTask.update { task }

    fun deleteProject(id: Uuid) {
        TODO()
    }

    @Stable
    fun interactionsFor(task: Uuid) = object : TaskInteractions {
        override fun onDelete() {
            deleteTask(task)
        }

        override fun onSelect() = selectedTask.update { TODO() }
    }

    fun createProject() {
        TODO("Not yet implemented")
    }

    fun getListProperties(key: ListId): StateFlow<TaskListProperties> {
        return MutableStateFlow(TaskListProperties(displayName = "Temp"))
    }
}
