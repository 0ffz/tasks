package me.dvyy.tasks.tasks.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message

expect class TasksLocalDataSource constructor() {
    fun saveList(key: ListId, list: TaskListModel)

    fun loadTasksForList(key: ListId): Result<TaskListModel?>

    fun getProjects(): List<ListId>

    fun deleteList(key: ListId)

    fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant = Clock.System.now())

//    inline fun getMessages(): List<Message<Uuid>>
}
