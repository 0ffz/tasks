package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties

class TaskListRepository(
    private val localStore: TasksLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend fun create(key: ListId, properties: TaskListProperties) = withContext(ioDispatcher) {
        localStore.createList(key, TaskListModel(properties))
    }

    suspend fun update(list: ListId, properties: TaskListProperties) = withContext(ioDispatcher) {
        localStore.setListProperties(list, properties)
    }

    fun observeProjects() = localStore.observeProjects()

    fun observeTasksFor(key: ListId): Flow<List<Task>> = localStore.observeListTasks(key)

    fun observeProperties(key: ListId): Flow<TaskListProperties> = localStore.observeListProperties(key)
}
