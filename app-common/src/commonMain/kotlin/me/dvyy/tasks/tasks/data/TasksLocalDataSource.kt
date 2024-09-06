package me.dvyy.tasks.tasks.data

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
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
import me.dvyy.tasks.model.*
import me.dvyy.tasks.model.database.RankFunctions
import me.dvyy.tasks.model.network.NetworkMessage
import me.dvyy.tasks.sync.data.MessagesDataSource

class TasksLocalDataSource(
    val database: Database,
    val messages: MessagesDataSource,
) {
    suspend fun createList(listId: ListId, list: TaskListModel) {
        database.listsQueries.transaction {
            val lastRank = database.listsQueries.lastRank().awaitAsOneOrNull() ?: 0
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


    suspend fun observeListTasks(listId: ListId): Flow<List<Task>> {
        val unranked = database.tasksQueries.forListWithoutRank(listId).awaitAsList()
        if (unranked.isNotEmpty()) database.tasksQueries.transaction {
            unranked.forEach {
                upsertRank(Rank(uuid = it.uuid, parent = listId.uuid, getRankAfterLast(listId)))
            }
        }
        return database.tasksQueries.forList(listId).asFlow().mapToList(Dispatchers.Default)
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

    suspend fun deleteList(listId: ListId) {
        database.listsQueries.delete(listId)
    }

    suspend fun getTask(taskId: TaskId): Task? {
        return database.tasksQueries.get(taskId).awaitAsOneOrNull()
    }

    suspend fun deleteTask(taskId: TaskId) {
        database.tasksQueries.delete(taskId)
    }


    suspend fun setListProperties(listId: ListId, props: TaskListProperties) {
        database.listsQueries.transaction {
            val list = database.listsQueries.get(listId).awaitAsOneOrNull()
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

    suspend fun upsertTask(task: Task) {
        database.tasksQueries.upsert(task)
    }

    suspend fun createTask(listId: ListId, atEndOfList: Boolean): Task {
        val rank = if (atEndOfList) getRankAfterLast(listId) else getRankBeforeFirst(listId)
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

    suspend fun getLastRankOrMiddle(listId: ListId) =
        (database.rankQueries.lastRank(listId.uuid).awaitAsOneOrNull() ?: RankFunctions.middleChar.toString())

    suspend fun getFirstRankOrMiddle(listId: ListId) =
        (database.rankQueries.firstRank(listId.uuid).awaitAsOneOrNull() ?: RankFunctions.middleChar.toString())

    suspend fun getRankAfterLast(listId: ListId): String {
        val lastRank = getLastRankOrMiddle(listId)
        return RankFunctions.getRankAfter(lastRank)
    }

    suspend fun getRankBeforeFirst(listId: ListId): String {
        val firstRank = getFirstRankOrMiddle(listId)
        return RankFunctions.getRankBefore(firstRank)
    }

    suspend fun getRankAfter(listId: ListId, rank: String): String {
        val lastRank = getLastRankOrMiddle(listId)
        return RankFunctions.getRankAfter(lastRank)
    }

    suspend fun upsertRank(rank: Rank) {
        database.rankQueries.upsert(rank)
        messages.saveMessage(NetworkMessage.Type.Update, rank.uuid, EntityType.RANK)
    }

    suspend fun getRankFor(task: TaskId): String? {
        return database.rankQueries.getRank(task.uuid).awaitAsOneOrNull()
    }

    /**
     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
     *
     * @return Whether task changed lists after the reorder.
     */
    suspend fun reorderTask(taskId: TaskId, destId: TaskId): Boolean = database.transactionWithResult {
        val task = getTask(taskId) ?: return@transactionWithResult false
        val dest = getTask(destId) ?: return@transactionWithResult false
        val changedLists = task.list != dest.list

        if (changedLists) moveTaskToList(taskId, dest.list)
        val taskRank = getRankFor(taskId) ?: RankFunctions.firstChar.toString()
        val destRank = getRankFor(destId) ?: RankFunctions.lastChar.toString()

        if (taskRank == destRank) return@transactionWithResult changedLists

        if (taskRank < destRank) {
            moveTaskAfter(dest.list, taskId, destRank)
        } else {
            moveTaskBefore(dest.list, taskId, destRank)
        }
        changedLists
    }

    suspend fun moveTaskToList(taskId: TaskId, listId: ListId) {
        database.tasksQueries.transaction {
            val task = getTask(taskId) ?: return@transaction
            val rank = getRankAfterLast(listId)
            upsertTask(task.copy(list = listId))
            upsertRank(Rank(taskId.uuid, listId.uuid, rank))
        }
    }

    suspend fun moveTaskBefore(
        list: ListId,
        task: TaskId,
        destRank: String,
    ) {
        val before = database.rankQueries.getRankBefore(list.uuid, destRank)
            .awaitAsOneOrNull()
            ?: RankFunctions.firstChar.toString()

        moveTaskBetween(list, task, before, destRank)
    }

    suspend fun moveTaskAfter(
        list: ListId,
        task: TaskId,
        destRank: String,
    ) {
        val after = database.rankQueries.getRankAfter(list.uuid, destRank)
            .awaitAsOneOrNull()
            ?: RankFunctions.lastChar.toString()

        moveTaskBetween(list, task, destRank, after)
    }

    suspend fun moveTaskBetween(
        list: ListId,
        task: TaskId,
        firstRank: String,
        secondRank: String,
    ) {
        val newRank = RankFunctions.getLexicographicMiddle(firstRank, secondRank)
        upsertRank(Rank(task.uuid, list.uuid, newRank))
    }
}
