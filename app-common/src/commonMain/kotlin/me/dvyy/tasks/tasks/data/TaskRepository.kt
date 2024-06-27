package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.withContext
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.NetworkMessage.Type.Delete
import me.dvyy.tasks.model.network.NetworkMessage.Type.Update
import me.dvyy.tasks.sync.data.MessagesDataSource
import me.dvyy.tasks.utils.AppDispatchers

//private const val KEY_DELETED_TASKS = "app-deleted-tasks"

class TaskRepository(
    private val localStore: TasksLocalDataSource,
    private val messages: MessagesDataSource,
) {
    private val dbContext = AppDispatchers.db

    suspend fun create(list: ListId): Task = withContext(dbContext) {
        localStore.createTask(list)
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
        localStore.moveTask(taskId, listId)
        messages.saveMessage(Update, taskId)
    }

    suspend fun reorder(from: TaskId, to: TaskId) = withContext(dbContext) {
        localStore.swapRank(from, to)
        messages.saveMessage(Update, from)
        messages.saveMessage(Update, to)
    }
}
