package me.dvyy.tasks.model.database.dao

import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.syncengine.schema.JsonTable
import me.dvyy.syncengine.schema.JsonView
import me.dvyy.tasks.model.components.ChildOfModel
import me.dvyy.tasks.model.rank.RankFunctions
import kotlin.uuid.Uuid

class ChildOfQueries(
    val table: JsonTable,
    val view: JsonView,
) {
    val queries = JsonDataQueries(ChildOfModel.serializer(), table)

    context(tx: Transaction)
    fun childrenOf(uuid: Uuid): List<Uuid> =
        tx.getList("SELECT id FROM $table WHERE data ->> 'parent' = ? ORDER BY data ->> 'rank'", uuid.toHexDashString()) {
            Uuid.fromByteArray(getBlob(0))
        }

    context(tx: WriteTransaction)
    fun moveTaskToList(uuid: Uuid, list: Uuid, atEnd: Boolean = true) {
        val rank = if (atEnd) getRankAfterLast(list) else getRankBeforeFirst(list)
        if (queries.get(uuid)?.parent != list)
            queries.upsert(uuid, ChildOfModel(list, rank))
//        queries.jsonSet(uuid, "$.parent", "'${list.toHexDashString()}'")
    }

    data class TaskRank(val parent: Uuid, val rank: String)

    context(tx: Transaction)
    fun getRankFor(task: Uuid): TaskRank? = tx
        .select("SELECT parent, rank FROM $view WHERE id = ? AND rank IS NOT NULL", task)
        .firstOrNull { TaskRank(Uuid.parseHexDash(getText(0)), getText(1)) }

    context(tx: Transaction)
    fun getLastRankInList(list: Uuid): String? = tx
        .select("SELECT rank FROM $view WHERE parent = ? ORDER BY rank DESC LIMIT 1", list.toHexDashString())
        .firstOrNull { getText(0) }

    context(tx: Transaction)
    fun getFirstRankInList(list: Uuid): String? = tx
        .select("SELECT rank FROM $view WHERE parent = ? ORDER BY rank LIMIT 1", list.toHexDashString())
        .firstOrNull { getText(0) }

    context(tx: Transaction)
    fun getLastTaskInList(list: Uuid): Uuid? = tx
        .select("SELECT id FROM $view WHERE parent = ? ORDER BY rank DESC LIMIT 1", list.toHexDashString())
        .firstOrNull { getUuid(0) }

    context(tx: Transaction)
    fun getFirstTaskInList(list: Uuid): Uuid? = tx
        .select("SELECT id FROM $view WHERE parent = ? ORDER BY rank LIMIT 1", list.toHexDashString())
        .firstOrNull { getUuid(0) }

    context(tx: Transaction)
    fun getRankAfterLast(list: Uuid): String = RankFunctions.getRankAfter(
        getLastRankInList(list) ?: RankFunctions.FIRST_CHAR.toString()
    )

    context(tx: Transaction)
    fun getRankBeforeFirst(list: Uuid): String = RankFunctions.getRankBefore(
        getFirstRankInList(list) ?: RankFunctions.LAST_CHAR.toString()
    )

    context(tx: WriteTransaction)
    fun moveToTask(task: Uuid, target: Uuid) {
        val taskRank = getRankFor(task) ?: return
        val targetRank = getRankFor(target) ?: return
        val parentsDiffer = taskRank.parent != targetRank.parent
        if (parentsDiffer) {
            moveTaskToList(task, targetRank.parent)
        }
        val nextRank = if (taskRank.rank > targetRank.rank || parentsDiffer) {
            getBefore(target)?.let { getRankFor(it)?.rank } ?: RankFunctions.FIRST_CHAR.toString()
        } else {
            getAfter(target)?.let { getRankFor(it)?.rank } ?: RankFunctions.LAST_CHAR.toString()
        }
        println(targetRank.rank + " " + nextRank)
        val middle = RankFunctions.getLexicographicMiddle(targetRank.rank, nextRank)
        setRank(task, middle)
        println("Moved $task to $middle")
//        //TODO better binds
//        val rank = tx.getOrNull(
//            "SELECT rank FROM subtask WHERE parent = ? and child = ?",
//            parent, other
//        ) { getText(0) } ?: RankFunctions.FIRST_CHAR.toString()
//        val next = tx.getOrNull(
//            "SELECT rank FROM subtask WHERE parent = ? and rank > ? LIMIT 1",
//            parent, rank
//        ) { getText(0) } ?: RankFunctions.LAST_CHAR.toString()
//        val middle: String = RankFunctions.getLexicographicMiddle(rank, next)
//        tx.getSingle(
//            "UPDATE subtask SET rank = ?, parent = ? WHERE parent = ? AND child = ?",
//            middle, parent, parent, other
//        ) {
//
//        }
    }

    //
    context(tx: Transaction)
    fun getAfter(id: Uuid): Uuid? {
        val (list, rank) = getRankFor(id) ?: return null
        return tx.getOrNull(
            "SELECT id FROM $view WHERE rank > ? AND parent = ? ORDER BY rank LIMIT 1",
            rank,
            list.toHexDashString()
        ) { getUuid(0) }
    }

    context(tx: Transaction)
    fun getBefore(id: Uuid): Uuid? {
        val (list, rank) = getRankFor(id) ?: return null
        return tx.getOrNull(
            "SELECT id FROM $view WHERE rank < ? AND parent = ? ORDER BY rank DESC LIMIT 1",
            rank,
            list.toHexDashString()
        ) { getUuid(0) }
    }

    context(tx: WriteTransaction)
    fun setRank(uuid: Uuid, rank: String) = queries.jsonSet(uuid, "$.rank", "'$rank'")

    /**
     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
     *
     * @return Whether task changed lists after the reorder.
     */
//    context(tx: WriteTransaction)
//    suspend fun reorder(source: TaskInList, destination: TaskInList): Boolean {
//        if (source.list != destination.list) TODO("change lists")
//
//        if (changedLists) moveTaskToList(taskId, dest.list)
//        val taskRank = getRankFor(source) ?: RankFunctions.firstChar.toString()
//        val destRank = getRankFor(destination) ?: RankFunctions.lastChar.toString()
//
//        if (taskRank == destRank) return@transactionWithResult changedLists
//
//        if (taskRank < destRank) {
//            moveAfter(destination.list, source.task, destination.task)
//        } else {
//            moveTaskBefore(dest.list, taskId, destRank)
//        }
//        changedLists
//    }
}

