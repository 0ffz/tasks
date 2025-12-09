package me.dvyy.tasks.model.database.dao

import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.tasks.model.components.Task
import kotlin.uuid.Uuid

//TODO filter owner when using get
class SubtaskRelationQueries(
    val tasks: JsonDataQueries<Task>,
) {
    context(tx: Transaction)
    fun childrenOf(uuid: Uuid): List<Uuid> = tx.getList("SELECT id FROM tasks WHERE parent = ?", uuid.toString()) {
        Uuid.fromByteArray(getBlob(0))
    }

    context(tx: WriteTransaction)
    fun moveTaskToList(uuid: Uuid, list: Uuid) {
        tasks.jsonSet(uuid, "$.parent", "'${list.toHexDashString()}'")
    }

    context(tx: Transaction)
    fun getRankFor(task: Uuid): String? =
        tx.getOrNull("SELECT id FROM tasks WHERE parent = ?", task) { getText(0) }

    //    context(tx: WriteTransaction)
//    fun moveAfter(parent: Uuid, child: Uuid, other: Uuid) {
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
//    }
//
    context(tx: Transaction)
    fun getAfter(id: Uuid): Uuid? {
        val rank = getRankFor(id) ?: return null
        return tx.getOrNull("SELECT id FROM tasks WHERE rank > ? LIMIT 1 ORDER BY rank", rank) { getUuid(0) }
    }

    context(tx: Transaction)
    fun getBefore(id: Uuid): Uuid? {
        val rank = getRankFor(id) ?: return null
        return tx.getOrNull("SELECT id FROM tasks WHERE rank < ? LIMIT 1 ORDER BY rank DESC", rank) { getUuid(0) }
    }

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

