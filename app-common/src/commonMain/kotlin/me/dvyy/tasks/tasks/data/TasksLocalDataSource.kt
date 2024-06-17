package me.dvyy.tasks.tasks.data

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.Message

expect class TasksLocalDataSource constructor() {
    fun saveList(key: ListKey, list: TaskListModel)

    fun loadTasksForList(key: ListKey): Result<TaskListModel?>

    fun getProjects(): List<ListKey.Project>

    fun deleteList(key: ListKey)

    fun saveMessage(type: Message.Type, uuid: Uuid, timestamp: Instant = Clock.System.now())

//    inline fun getMessages(): List<Message<Uuid>>
}
