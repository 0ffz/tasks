package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.database.helpers.KeyHelpers.frontMatter
import me.dvyy.tasks.model.database.RankFunctions
//import org.dizitart.kno2.documentOf
//import org.dizitart.no2.collection.FindOptions
//import org.dizitart.no2.common.SortOrder
//
//class RankDataSource(
//    val vault: Vault,
//) {
//    fun getRankOrMiddle(list: VaultPath, order: SortOrder) = vault.query(
//        TaskFilters.tasksForList(list), FindOptions.orderBy(frontMatter("sortOrder"), order).limit(1)
//    ).project(documentOf(frontMatter("sortOrder") to null))
//        .firstOrNull()
//        .frontMatter<String>("sortOrder")
//        ?: RankFunctions.middleChar.toString()
//
//    //
////    suspend fun getFirstRankOrMiddle(listId: ListId) =
////        (database.rankQueries.firstRank(listId.uuid).awaitAsOneOrNull() ?: RankFunctions.middleChar.toString())
////
//    fun getRankAfterLast(list: VaultPath): String {
//        val lastRank = getRankOrMiddle(list, SortOrder.Descending)
//        return RankFunctions.getRankAfter(lastRank)
//    }
//    //
//    fun getRankBeforeFirst(listId: VaultPath): String {
//        val firstRank = getRankOrMiddle(listId, SortOrder.Ascending)
//        return RankFunctions.getRankBefore(firstRank)
//    }
////
////    suspend fun getRankAfter(listId: ListId, rank: String): String {
////        val lastRank = getLastRankOrMiddle(listId)
////        return RankFunctions.getRankAfter(lastRank)
////    }
////
////    suspend fun upsertRank(rank: Rank) {
////        database.rankQueries.upsert(rank)
////        messages.saveMessage(NetworkMessage.Type.Update, rank.uuid, EntityType.RANK)
////    }
////
////    suspend fun getRankFor(task: TaskId): String? {
////        return database.rankQueries.getRank(task.uuid).awaitAsOneOrNull()
////    }
////
////    /**
////     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
////     *
////     * @return Whether task changed lists after the reorder.
////     */
////    suspend fun reorderTask(taskId: TaskId, destId: TaskId): Boolean = database.transactionWithResult {
////        val task = getTask(taskId) ?: return@transactionWithResult false
////        val dest = getTask(destId) ?: return@transactionWithResult false
////        val changedLists = task.list != dest.list
////
////        if (changedLists) moveTaskToList(taskId, dest.list)
////        val taskRank = getRankFor(taskId) ?: RankFunctions.firstChar.toString()
////        val destRank = getRankFor(destId) ?: RankFunctions.lastChar.toString()
////
////        if (taskRank == destRank) return@transactionWithResult changedLists
////
////        if (taskRank < destRank) {
////            moveTaskAfter(dest.list, taskId, destRank)
////        } else {
////            moveTaskBefore(dest.list, taskId, destRank)
////        }
////        changedLists
////    }
////
////    suspend fun moveTaskToList(taskId: TaskId, listId: ListId) {
////        vault.createDocument()
////        database.tasksQueries.transaction {
////            val task = getTask(taskId) ?: return@transaction
////            val rank = getRankAfterLast(listId)
////            upsertTask(task.copy(list = listId))
////            upsertRank(Rank(taskId.uuid, listId.uuid, rank))
////        }
////    }
////
////    suspend fun moveTaskBefore(
////        list: ListId,
////        task: TaskId,
////        destRank: String,
////    ) {
////        val before = database.rankQueries.getRankBefore(list.uuid, destRank)
////            .awaitAsOneOrNull()
////            ?: RankFunctions.firstChar.toString()
////
////        moveTaskBetween(list, task, before, destRank)
////    }
////
////    suspend fun moveTaskAfter(
////        list: ListId,
////        task: TaskId,
////        destRank: String,
////    ) {
////        val after = database.rankQueries.getRankAfter(list.uuid, destRank)
////            .awaitAsOneOrNull()
////            ?: RankFunctions.lastChar.toString()
////
////        moveTaskBetween(list, task, destRank, after)
////    }
////
////    suspend fun moveTaskBetween(
////        list: ListId,
////        task: TaskId,
////        firstRank: String,
////        secondRank: String,
////    ) {
////        val newRank = RankFunctions.getLexicographicMiddle(firstRank, secondRank)
////        upsertRank(Rank(task.uuid, list.uuid, newRank))
////    }
//}
