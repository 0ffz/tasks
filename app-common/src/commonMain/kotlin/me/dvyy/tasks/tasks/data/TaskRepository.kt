package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.withContext
import me.dvyy.syncengine.db.Database
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.NetworkMessage.Type.Delete
import me.dvyy.tasks.model.network.NetworkMessage.Type.Update
import me.dvyy.tasks.sync.data.MessagesDataSource

//private const val KEY_DELETED_TASKS = "app-deleted-tasks"

class TaskRepository(
    private val localStore: TasksLocalDataSource,
    private val messages: MessagesDataSource,
) {
    suspend fun create(list: ListId, atEndOfList: Boolean): Task = Database.write {
        localStore.createTask(list, atEndOfList)
        // Don't save a message until the task is modified
    }

    suspend fun update(
        taskId: TaskId,
        updater: (Task) -> Task,
    ) = withContext(dbContext) {
        val task = localStore.getTask(taskId) ?: return@withContext
        localStore.upsertTask(updater(task))
        messages.saveMessage(Update, taskId)
    }

    suspend fun delete(taskId: TaskId) = withContext(dbContext) {
        localStore.deleteTask(taskId)
        messages.saveMessage(Delete, taskId)
    }

    suspend fun move(taskId: TaskId, listId: ListId) = withContext(dbContext) {
        localStore.moveTaskToList(taskId, listId)
        messages.saveMessage(Update, taskId)
    }

    suspend fun moveTaskTo(taskId: TaskId, destId: TaskId) = withContext(dbContext) {
        val changedLists = localStore.reorderTask(taskId, destId)
        messages.saveMessage(Update, taskId.uuid, EntityType.RANK)
        if (changedLists) messages.saveMessage(Update, taskId)
    }
}
