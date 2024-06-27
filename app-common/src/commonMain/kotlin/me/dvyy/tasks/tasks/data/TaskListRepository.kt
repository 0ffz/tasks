package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.network.NetworkMessage.Type.Update
import me.dvyy.tasks.sync.data.MessagesDataSource
import me.dvyy.tasks.utils.AppDispatchers

class TaskListRepository(
    private val localStore: TasksLocalDataSource,
    private val messages: MessagesDataSource,
) {
    private val dbContext = AppDispatchers.db

    suspend fun create(listId: ListId, properties: TaskListProperties) = withContext(dbContext) {
        localStore.createList(listId, TaskListModel(properties))
        messages.saveMessage(Update, listId)
    }

    suspend fun update(listId: ListId, properties: TaskListProperties) = withContext(dbContext) {
        localStore.setListProperties(listId, properties)
        messages.saveMessage(Update, listId)
    }

    fun observeProjects() =
        localStore.observeProjects()

    fun observeTasksFor(key: ListId): Flow<List<Task>> = localStore.observeListTasks(key)

    fun observeProperties(key: ListId): Flow<TaskListProperties> = localStore.observeListProperties(key)
}
