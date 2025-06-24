package me.dvyy.tasks.model.schema

import me.dvyy.syncengine.db.Transaction
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.db.tables.Table
import me.dvyy.tasks.model.components.TaskInList
import me.dvyy.tasks.model.database.RankFunctions
import java.util.*
import kotlin.uuid.Uuid

class RelationTableDAO<T>(
    val table: Table,
) {
    context(tx: Transaction)
    fun childrenOf(uuid: Uuid): List<Uuid> = tx.getList("SELECT child FROM subtask") {
        Uuid.fromByteArray(getBlob(0))
    }

    context(tx: Transaction)
    fun getRankFor(row: TaskInList): String? =
        tx.getSingle("SELECT row FROM $table WHERE parent = ? and child = ?", row.list, row.task) { getText(0) }

    context(tx: WriteTransaction)
    fun moveAfter(parent: Uuid, child: Uuid, other: Uuid) {
        //TODO better binds
        val rank = tx.getOrNull(
            "SELECT rank FROM subtask WHERE parent = ? and child = ?",
            parent, other
        ) { getText(0) } ?: RankFunctions.firstChar.toString()
        val next = tx.getOrNull(
            "SELECT rank FROM subtask WHERE parent = ? and rank > ? LIMIT 1",
            parent, rank
        ) { getText(0) } ?: RankFunctions.lastChar.toString()
        val middle: String = RankFunctions.getLexicographicMiddle(rank, next)
        tx.getSingle(
            "UPDATE subtask SET rank = ?, parent = ? WHERE parent = ? AND child = ?",
            middle, parent, parent, other
        ) {

        }
    }

    context(tx: Transaction)
    fun getAfter(id: Uuid): Uuid? = TODO()

    context(tx: Transaction)
    fun getBefore(id: Uuid): Uuid? = TODO()

    /**
     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
     *
     * @return Whether task changed lists after the reorder.
     */
//    context(tx: WriteTransaction)
//    suspend fun reorder(source: TaskInList, destination: TaskInList): Boolean {
//        if (source.list != destination.list) TODO("change lists")
//        val changedLists = task.list != dest.list
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

