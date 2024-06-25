package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.utils.AppDispatchers

class TaskListRepository(
    private val localStore: TasksLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val dbContext = AppDispatchers.db

    suspend fun create(key: ListId, properties: TaskListProperties) = withContext(dbContext) {
        localStore.createList(key, TaskListModel(properties))
    }

    suspend fun update(list: ListId, properties: TaskListProperties) = withContext(dbContext) {
        localStore.setListProperties(list, properties)
    }

    fun observeProjects() =
        localStore.observeProjects()

    fun observeTasksFor(key: ListId): Flow<List<Task>> = localStore.observeListTasks(key)

    fun observeProperties(key: ListId): Flow<TaskListProperties> = localStore.observeListProperties(key)
}
