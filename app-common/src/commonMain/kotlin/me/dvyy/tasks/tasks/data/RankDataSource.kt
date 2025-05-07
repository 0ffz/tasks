package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultDataSource
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.model.database.RankFunctions

class RankDataSource(
    val vault: Vault,
) {
//    fun getRankOrMiddle(list: VaultPath, order: SortOrder) = vault.query(
//        TaskFilters.tasksForList(list), FindOptions.orderBy(frontMatter("sortOrder"), order).limit(1)
//    ).project(documentOf(frontMatter("sortOrder") to null))
//        .firstOrNull()
//        .frontMatter<String>("sortOrder")
//        ?: RankFunctions.middleChar.toString()

    //
//    suspend fun getFirstRankOrMiddle(listId: ListId) =
//        (database.rankQueries.firstRank(listId.uuid).awaitAsOneOrNull() ?: RankFunctions.middleChar.toString())
//
//    fun getRankAfterLast(list: VaultPath): String {
//        val lastRank = getRankOrMiddle(list, SortOrder.Descending)
//        return RankFunctions.getRankAfter(lastRank)
//    }
//
//    //
//    fun getRankBeforeFirst(listId: VaultPath): String {
//        val firstRank = getRankOrMiddle(listId, SortOrder.Ascending)
//        return RankFunctions.getRankBefore(firstRank)
//    }
//
    /**
     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
     *
     * @return Whether task changed lists after the reorder.
     */
    fun reorderTask(taskId: VaultPath, destId: VaultPath): Boolean {
        val task = vault.getNote(taskId) ?: return false
        val dest = vault.getNote(destId) ?: return false
        val destProject = VaultPath(dest.frontMatter.projects.firstOrNull() ?: return false)
        val changedLists = task.frontMatter.projects != dest.frontMatter.projects

        //TODO
//        if (changedLists) moveTaskToList(taskId, dest.list)

        val taskRank = task.frontMatter.sortOrder ?: RankFunctions.firstChar.toString()
        val destRank = dest.frontMatter.sortOrder ?: RankFunctions.lastChar.toString()

        println("curr: $taskRank, dest: $destRank, changed: $changedLists")

        if (taskRank == destRank) return changedLists
        if (taskRank < destRank) {
            moveTaskAfter(destProject, taskId, destRank)
        } else {
            moveTaskBefore(destProject, taskId, destRank)
        }
        return changedLists
    }

    //
//    suspend fun moveTaskToList(taskId: TaskId, listId: ListId) {
//        vault.createDocument()
//        database.tasksQueries.transaction {
//            val task = getTask(taskId) ?: return@transaction
//            val rank = getRankAfterLast(listId)
//            upsertTask(task.copy(list = listId))
//            upsertRank(Rank(taskId.uuid, listId.uuid, rank))
//        }
//    }
//
    fun moveTaskBefore(
        list: VaultPath,
        task: VaultPath,
        destRank: String,
    ) {
        val before = vault.getBacklinks(NoteFrontMatter::projects, list).itemBefore(destRank)
            ?.frontMatter?.sortOrder ?: RankFunctions.firstChar.toString()
        moveTaskBetween(list, task, before, destRank)
    }

    //
    fun moveTaskAfter(
        list: VaultPath,
        task: VaultPath,
        destRank: String,
    ) {
        val after = vault.getBacklinks(NoteFrontMatter::projects, list).itemAfter(destRank)
            ?.frontMatter?.sortOrder ?: RankFunctions.lastChar.toString()
        moveTaskBetween(list, task, destRank, after)
    }

    //
    fun moveTaskBetween(
        list: VaultPath,
        task: VaultPath,
        firstRank: String,
        secondRank: String,
    ) {
        val newRank = RankFunctions.getLexicographicMiddle(firstRank, secondRank)
        vault.update(task) {
            frontMatter {
                projects = listOf(list.pathWithoutExt)
                sortOrder = newRank
            }
        }
    }
}
