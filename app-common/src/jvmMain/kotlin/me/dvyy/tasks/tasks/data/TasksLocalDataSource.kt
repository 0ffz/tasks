package me.dvyy.tasks.tasks.data

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.model.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor(
    val database: Database,
) {

    actual fun saveList(listId: ListId, list: TaskListModel) {
        database.listsQueries.insert(
            uuid = listId.uuid,
            title = list.properties.displayName,
        )
        database.tasksQueries.transaction {
            list.tasks.forEach {
                database.tasksQueries.insert(
                    uuid = it.id.uuid,
                    list = listId.uuid,
                    text = it.text,
                    completed = it.completed,
                    highlight = it.highlight,
                )
            }
        }
        //TODO remove deleted paths
    }

    actual fun loadTasksForList(listId: ListId): Result<TaskListModel?> {
        val list = database.listsQueries.get(listId.uuid).executeAsOneOrNull()
        val tasks = database.tasksQueries.forList(listId.uuid).executeAsList()
        return TaskListModel(
            properties = TaskListProperties(
                displayName = list?.title,
                date = listId.date,
            ), //TODO read properties separately
            tasks = tasks.map {
                TaskModel(
                    TaskId(it.uuid),
                    it.text ?: "",
                    it.completed,
                    it.highlight
                )
            },
        ).let { Result.success(it) }
    }

    actual fun getProjects(): List<ListId> {
        return database.listsQueries.getProjects().executeAsList().map { ListId(it) }
    }

    actual fun deleteList(listId: ListId) {
        database.listsQueries.delete(listId.uuid)
    }

    actual fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant) {
        database.messagesQueries.insert(uuid.uuid, timestamp, type)
    }
}
