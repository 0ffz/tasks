package me.dvyy.tasks.tasks.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message

expect class TasksLocalDataSource constructor(database: Database) {
    fun saveList(listId: ListId, list: TaskListModel)

    fun loadTasksForList(listId: ListId): Result<TaskListModel?>

    fun getProjects(): List<ListId>

    fun deleteList(listId: ListId)

    fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant = Clock.System.now())

//    inline fun getMessages(): List<Message<Uuid>>
}
