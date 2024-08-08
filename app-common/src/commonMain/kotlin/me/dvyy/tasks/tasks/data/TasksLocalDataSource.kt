package me.dvyy.tasks.tasks.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.db.client.Database
import me.dvyy.tasks.db.client.Rank
import me.dvyy.tasks.db.client.Task
import me.dvyy.tasks.db.client.TaskList
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.model.database.RankFunctions

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
        val unranked = database.tasksQueries.forListWithoutRank(listId).executeAsList()
        if (unranked.isNotEmpty()) database.tasksQueries.transaction {
            unranked.forEach {
                upsertRank(Rank(uuid = it.uuid, parent = listId.uuid, getRankAfterLast(listId)))
            }
        }
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
        val rank = getRankAfterLast(listId)
        val task = Task(
            uuid = TaskId.new(),
            list = listId,
            completed = false,
            text = "",
            highlight = Highlight.Unmarked,
        )
        upsertTask(task)
        upsertRank(rank = Rank(task.uuid.uuid, listId.uuid, rank))
        return task
    }

    // Rank functions

    fun getLastRankOrMiddle(listId: ListId) =
        (database.rankQueries.lastRank(listId.uuid).executeAsOneOrNull() ?: RankFunctions.middleChar.toString())

    fun getRankAfterLast(listId: ListId): String {
        val lastRank = getLastRankOrMiddle(listId)
        return RankFunctions.getRankAfter(lastRank)
    }

    fun getRankAfter(listId: ListId, rank: String): String {
        val lastRank = getLastRankOrMiddle(listId)
        return RankFunctions.getRankAfter(lastRank)
    }

    fun upsertRank(rank: Rank) {
        database.rankQueries.upsert(rank)
    }

    fun getRankFor(task: TaskId): String? {
        return database.rankQueries.getRank(task.uuid).executeAsOneOrNull()
    }

    fun reorderTask(taskId: TaskId, destId: TaskId) = database.transaction {
        val task = getTask(taskId) ?: return@transaction
        val dest = getTask(destId) ?: return@transaction

        if (task.list != dest.list) moveTaskToList(taskId, dest.list)
        val taskRank = getRankFor(taskId) ?: RankFunctions.firstChar.toString()
        val destRank = getRankFor(destId) ?: RankFunctions.lastChar.toString()

        if (taskRank == destRank) return@transaction

        if (taskRank < destRank) {
            moveTaskAfter(dest.list, taskId, destRank)
        } else {
            moveTaskBefore(dest.list, taskId, destRank)
        }
    }

    fun moveTaskToList(taskId: TaskId, listId: ListId) {
        database.tasksQueries.transaction {
            val task = getTask(taskId) ?: return@transaction
            val rank = getRankAfterLast(listId)
            upsertTask(task.copy(list = listId))
            upsertRank(Rank(taskId.uuid, listId.uuid, rank))
        }
    }

    fun moveTaskBefore(
        list: ListId,
        task: TaskId,
        destRank: String,
    ) {
        val before = database.rankQueries.getRankBefore(list.uuid, destRank)
            .executeAsOneOrNull()
            ?: RankFunctions.firstChar.toString()

        moveTaskBetween(list, task, before, destRank)
    }

    fun moveTaskAfter(
        list: ListId,
        task: TaskId,
        destRank: String,
    ) {
        val after = database.rankQueries.getRankAfter(list.uuid, destRank)
            .executeAsOneOrNull()
            ?: RankFunctions.lastChar.toString()

        moveTaskBetween(list, task, destRank, after)
    }

    fun moveTaskBetween(
        list: ListId,
        task: TaskId,
        firstRank: String,
        secondRank: String,
    ) {
        val newRank = RankFunctions.getLexicographicMiddle(firstRank, secondRank)
        database.rankQueries.upsert(Rank(task.uuid, list.uuid, newRank))
    }
}
