package me.dvyy.tasks.tasks.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.*

class TasksLocalDataSource(
    val database: Database,
) {
    fun createList(listId: ListId, list: TaskListModel) {
        database.listsQueries.insert(
            uuid = listId,
            isProject = !listId.isDate,
            title = list.properties.displayName,
        )
        database.tasksQueries.transaction {
            list.tasks.forEach {
                database.tasksQueries.upsert(it)
            }
        }
    }


    fun observeListTasks(listId: ListId): Flow<List<Task>> {
        return database.tasksQueries.forList(listId).asFlow()
            .mapToList(Dispatchers.Default)
    }


    fun observeListProperties(listId: ListId): Flow<TaskListProperties> {
        return database.listsQueries.get(listId).asFlow()
            .mapToOneOrDefault(
                TaskList(
                    listId,
                    isProject = !listId.isDate,
                    title = null,
                ), Dispatchers.Default
            )
            .map {
                TaskListProperties(
                    displayName = it.title,
                    date = listId.date,
                )
            }
    }

//
//    fun loadTasksForList(listId: ListId): Result<TaskListModel?> {
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

    fun observeProjects(): Flow<List<ListId>> {
        return database.listsQueries.getProjects().asFlow().mapToList(Dispatchers.Default)
    }

    fun deleteList(listId: ListId) {
        database.listsQueries.delete(listId)
    }

    fun getTask(taskId: TaskId): Task? {
        return database.tasksQueries.get(taskId).executeAsOneOrNull()
    }

    fun deleteTask(taskId: TaskId) {
        database.tasksQueries.delete(taskId)
    }

    fun swapRank(from: TaskId, to: TaskId) {
        database.tasksQueries.transaction {
            val fromTask = database.tasksQueries.get(from).executeAsOne()
            val toTask = database.tasksQueries.get(to).executeAsOne()
            database.tasksQueries.upsert(fromTask.copy(rank = toTask.rank))
            database.tasksQueries.upsert(toTask.copy(rank = fromTask.rank))
        }
    }

    fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant = Clock.System.now()) {
        database.messagesQueries.insert(uuid.uuid, timestamp, type)
    }

    fun setListProperties(listId: ListId, props: TaskListProperties) {
        database.listsQueries.insert(
            uuid = listId,
            isProject = !listId.isDate,
            title = props.displayName,
        )
    }

    fun upsertTask(task: Task) {
        database.tasksQueries.upsert(task)
    }

    fun createTask(listId: ListId): Task {
        val rank = database.tasksQueries.listSize(listId).executeAsOne()
        val task = Task(
            uuid = TaskId.new(),
            list = listId,
            completed = false,
            text = "",
            highlight = Highlight.Unmarked,
            rank = rank,
        )
        upsertTask(task)
        return task
    }

    fun moveTask(taskId: TaskId, listId: ListId) {
        database.tasksQueries.transaction {
            val task = getTask(taskId) ?: return@transaction
            val rank = getListSize(listId)
            upsertTask(task.copy(list = listId, rank = rank))
        }
    }

    fun getListSize(listId: ListId) = database.tasksQueries.listSize(listId).executeAsOne()
}
