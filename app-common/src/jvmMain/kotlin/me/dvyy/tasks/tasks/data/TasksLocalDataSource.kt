package me.dvyy.tasks.tasks.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor(
    val database: Database,
) {

    actual fun saveList(listId: ListId, list: TaskListModel) {
        database.listsQueries.insert(
            uuid = listId.uuid,
            isProject = !listId.isDate,
            title = list.properties.displayName,
        )
        database.tasksQueries.transaction {
            list.tasks.forEachIndexed { index, it ->
                database.tasksQueries.insert(
                    uuid = it.id.uuid,
                    list = listId.uuid,
                    text = it.text,
                    completed = it.completed,
                    highlight = it.highlight,
                    rank = index.toLong(),
                )
            }
        }
        //TODO remove deleted paths
    }


    actual fun getTasksForList(listId: ListId): Flow<List<TaskModel>> {
        return database.tasksQueries.forList(listId.uuid).asFlow()
            .mapToList(Dispatchers.IO)
            .map { tasks ->
                tasks.map {
                    TaskModel(
                        TaskId(it.uuid),
                        it.text ?: "",
                        it.completed,
                        it.highlight
                    )
                }
            }
    }


    actual fun getListProperties(listId: ListId): Flow<TaskListProperties> {
        return database.listsQueries.get(listId.uuid).asFlow()
            .mapToOneOrDefault(
                TaskList(
                    listId.uuid,
                    isProject = !listId.isDate,
                    title = null,
                ), Dispatchers.IO
            )
            .map {
                TaskListProperties(
                    displayName = it.title,
                    date = listId.date,
                )
            }
    }

//
//    actual fun loadTasksForList(listId: ListId): Result<TaskListModel?> {
//        val list = database.listsQueries.get(listId.uuid).executeAsOneOrNull()
//        return TaskListModel(
//            properties = TaskListProperties(
//                displayName = list?.title,
//                date = listId.date,
//            ), //TODO read properties separately
//            tasks = tasks.map {
//            },
//        ).let { Result.success(it) }
//    }

    actual fun getProjects(): Flow<List<ListId>> {
        return database.listsQueries.getProjects().asFlow().mapToList(Dispatchers.IO).map { uuids ->
            uuids.map { it.asList() }
        }
//        return database.listsQueries.getProjects().executeAsList().map { ListId(it) }
    }

    actual fun deleteList(listId: ListId) {
        database.listsQueries.delete(listId.uuid)
    }

    actual fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant) {
        database.messagesQueries.insert(uuid.uuid, timestamp, type)
    }

    actual fun setListProperties(listId: ListId, props: TaskListProperties) {
        database.listsQueries.insert(
            uuid = listId.uuid,
            isProject = !listId.isDate,
            title = props.displayName,
        )
    }
}
