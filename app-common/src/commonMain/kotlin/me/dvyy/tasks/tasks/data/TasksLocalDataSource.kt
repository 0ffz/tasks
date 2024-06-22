package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.model.*

expect class TasksLocalDataSource constructor(database: Database) {
    fun saveList(listId: ListId, list: TaskListModel)

    fun getTasksForList(listId: ListId): Flow<List<TaskModel>>

    fun getListProperties(listId: ListId): Flow<TaskListProperties>

    fun setListProperties(listId: ListId, props: TaskListProperties)
//    fun loadTasksForList(listId: ListId): Result<TaskListModel?>

    fun getProjects(): Flow<List<ListId>>

    fun deleteList(listId: ListId)

    fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant = Clock.System.now())

//    inline fun getMessages(): List<Message<Uuid>>
}
