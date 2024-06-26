package me.dvyy.tasks.tasks.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties

class TasksLocalDataSource(
    val database: Database,
) {
    fun createList(listId: ListId, list: TaskListModel) {
        database.listsQueries.transaction {
            val lastRank = database.listsQueries.lastRank().executeAsOneOrNull() ?: 0
            database.listsQueries.insert(
                TaskList(
                    uuid = listId,
                    isProject = !listId.isDate,
                    title = list.properties.displayName,
                    rank = lastRank + 1
                )
            )

        }
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
                    rank = 0,
                ), Dispatchers.Default
            )
            .map {
                TaskListProperties(
                    displayName = it.title,
                    date = listId.date,
                )
            }
    }

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
        if (from == to) return
        database.tasksQueries.transaction {
            val fromTask = database.tasksQueries.get(from).executeAsOne()
            val toTask = database.tasksQueries.get(to).executeAsOne()

            val prevSlotFree =
                database.tasksQueries.isRankAvailable(toTask.list, toTask.rank - 1).executeAsOneOrNull() == null

            if (prevSlotFree) {
                database.tasksQueries.upsert(fromTask.copy(rank = toTask.rank - 1, list = toTask.list))
                return@transaction
            }

            if (fromTask.list != toTask.list) {
                database.tasksQueries.shiftRanksDown(toTask.list, toTask.rank)
                database.tasksQueries.upsert(fromTask.copy(rank = toTask.rank, list = toTask.list))
            } else {
                database.tasksQueries.upsert(fromTask.copy(rank = toTask.rank))
                database.tasksQueries.upsert(toTask.copy(rank = fromTask.rank))
            }
        }
    }

    fun setListProperties(listId: ListId, props: TaskListProperties) {
        database.listsQueries.transaction {
            val list = database.listsQueries.get(listId).executeAsOneOrNull()
            database.listsQueries.insert(
                TaskList(
                    uuid = listId,
                    isProject = !listId.isDate,
                    title = props.displayName,
                    rank = list?.rank ?: 0
                )
            )
        }
    }

    fun upsertTask(task: Task) {
        database.tasksQueries.upsert(task)
    }

    fun createTask(listId: ListId): Task {
        val rank = getNextRank(listId)
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
            val rank = getNextRank(listId)
            upsertTask(task.copy(list = listId, rank = rank))
        }
    }

    fun getNextRank(listId: ListId) = (database.tasksQueries.lastRank(listId).executeAsOneOrNull() ?: 0) + 1
}
