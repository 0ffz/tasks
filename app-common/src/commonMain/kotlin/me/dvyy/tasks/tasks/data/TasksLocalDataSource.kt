package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.model.*

expect class TasksLocalDataSource constructor(database: Database) {
    fun saveList(listId: ListId, list: TaskListModel)

    fun observeListTasks(listId: ListId): Flow<List<Task>>

    fun observeListProperties(listId: ListId): Flow<TaskListProperties>

    fun setListProperties(listId: ListId, props: TaskListProperties)
//    fun loadTasksForList(listId: ListId): Result<TaskListModel?>

    fun observeProjects(): Flow<List<ListId>>

    fun deleteList(listId: ListId)

    fun getTask(taskId: TaskId): Task?

    fun swapRank(from: TaskId, to: TaskId)

    fun deleteTask(taskId: TaskId)

    fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant = Clock.System.now())


    fun upsertTask(task: Task)
//    inline fun getMessages(): List<Message<Uuid>>
}
