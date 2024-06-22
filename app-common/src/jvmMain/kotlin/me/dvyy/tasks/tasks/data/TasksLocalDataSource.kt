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
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor(
    val database: Database,
) {

    actual fun saveList(listId: ListId, list: TaskListModel) {
        database.listsQueries.insert(
            uuid = listId,
            isProject = !listId.isDate,
            title = list.properties.displayName,
        )
        database.tasksQueries.transaction {
            list.tasks.forEachIndexed { index, it ->
                database.tasksQueries.upsert(
                    Task(
                        uuid = it.id,
                        list = listId,
                        text = it.text,
                        completed = it.completed,
                        highlight = it.highlight,
                        rank = index.toLong(),
                    )
                )
            }
        }
        //TODO remove deleted paths
    }


    actual fun observeListTasks(listId: ListId): Flow<List<Task>> {
        return database.tasksQueries.forList(listId).asFlow()
            .mapToList(Dispatchers.IO)
    }


    actual fun observeListProperties(listId: ListId): Flow<TaskListProperties> {
        return database.listsQueries.get(listId).asFlow()
            .mapToOneOrDefault(
                TaskList(
                    listId,
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

    actual fun observeProjects(): Flow<List<ListId>> {
        return database.listsQueries.getProjects().asFlow().mapToList(Dispatchers.IO)
    }

    actual fun deleteList(listId: ListId) {
        database.listsQueries.delete(listId)
    }

    actual fun getTask(taskId: TaskId): Task? {
        return database.tasksQueries.get(taskId).executeAsOneOrNull()
    }

    actual fun deleteTask(taskId: TaskId) {
        database.tasksQueries.delete(taskId)
    }

    actual fun swapRank(from: TaskId, to: TaskId) {
        database.tasksQueries.transaction {
            val fromTask = database.tasksQueries.get(from).executeAsOne()
            val toTask = database.tasksQueries.get(to).executeAsOne()
            database.tasksQueries.upsert(fromTask.copy(rank = toTask.rank))
            database.tasksQueries.upsert(toTask.copy(rank = fromTask.rank))
        }
    }

    actual fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant) {
        database.messagesQueries.insert(uuid.uuid, timestamp, type)
    }

    actual fun setListProperties(listId: ListId, props: TaskListProperties) {
        database.listsQueries.insert(
            uuid = listId,
            isProject = !listId.isDate,
            title = props.displayName,
        )
    }

    actual fun upsertTask(task: Task) {
        database.tasksQueries.upsert(task)
    }
}
