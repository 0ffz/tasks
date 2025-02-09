package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.flow.Flow
import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.content
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.database.helpers.DocumentHelpers.read
import me.dvyy.tasks.database.helpers.DocumentHelpers.vaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.write
import me.dvyy.tasks.database.helpers.KeyHelpers.frontMatter
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.asList
import me.dvyy.tasks.database.helpers.NitriteFlowHelpers.project
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.database.RankFunctions
import me.dvyy.tasks.tasks.ui.elements.list.TaskUiStateWithPath
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.filters.elemMatch
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder

class TasksLocalDataSource(
    val vault: Vault,
//    val messages: MessagesDataSource,
) {
    fun moveTask(task: VaultPath, /*from: VaultPath,*/ to: VaultPath) {
        var moveDocumentTo: VaultPath? = null
        vault.update(task, frontMatter = {
//            val updatedProjects = (it.read<List<String>>("projects") ?: listOf()).minus(from.pathWithoutExt).plus(to.pathWithoutExt)
            val updatedProjects = listOf(to.pathWithoutExt)
            it.write("projects", updatedProjects)
        })

        vault.getDocument(task)?.let {
            if (it.read<Boolean>(frontMatter("managed")) == true) {
                val vaultPath = it.vaultPath()
                val targetFolder = vault.taskFolderFor(to)
                if (vaultPath.parent != targetFolder) {
                    moveDocumentTo = targetFolder.resolve(vaultPath.displayName + ".md")
                }
            }
        }
//        if (moveDocumentTo != null) vault.moveDocument(task, moveDocumentTo)
    }

    //    val markdownChecklistRegex = "^- \\[[xX ]]".toRegex()
    fun observeListTasks(listId: VaultPath): Flow<List<TaskUiStateWithPath>> {
//        vault.query(KeyHelpers.PATH_KEY eq listId.pathString).project(KeyHelpers.CONTENT_KEY).map {
//            val content = it.single().content()
//            content.lineSequence().filter { it.trim().matches(markdownChecklistRegex) }
//        }
        return vault.queryAsFlow(
            frontMatter("projects") elemMatch ("$" eq listId.pathWithoutExt), FindOptions.orderBy(
                frontMatter("sortOrder"),
                SortOrder.Ascending
            )
        ).project(frontMatter("projects"), frontMatter("done"), "fileContent", "path").asList {
            TaskUiStateWithPath(
                state = TaskUiState(
                    text = (it.content()),
                    completed = it.frontMatter<Boolean?>("done") ?: false,
                    highlight = Highlight.Unmarked,
                ),
                path = it.vaultPath()
            )
        }
    }

//    fun observeProjects(): Flow<List<ListId>> {
////        return database.listsQueries.getProjects().asFlow().mapToList(Dispatchers.Default)
//    }

//    suspend fun setListProperties(listId: ListId, props: TaskListProperties) {
//        database.listsQueries.transaction {
//            val list = database.listsQueries.get(listId).awaitAsOneOrNull()
//            database.listsQueries.insert(
//                TaskList(
//                    uuid = listId,
//                    isProject = !listId.isDate,
//                    title = props.displayName,
//                    rank = list?.rank ?: 0
//                )
//            )
//        }

    //    // Rank functions
//
    fun getLastRankOrMiddle(list: VaultPath) = vault.query(
        frontMatter("projects") elemMatch ("$" eq list.pathWithoutExt), FindOptions.orderBy(
            frontMatter("sortOrder"),
            SortOrder.Descending
        )
    ).project(documentOf(frontMatter("sortOrder") to null))
        .firstOrNull()
        .frontMatter<String>("sortOrder")
        ?: RankFunctions.middleChar.toString()

    //
//    suspend fun getFirstRankOrMiddle(listId: ListId) =
//        (database.rankQueries.firstRank(listId.uuid).awaitAsOneOrNull() ?: RankFunctions.middleChar.toString())
//
    fun getRankAfterLast(list: VaultPath): String {
        val lastRank = getLastRankOrMiddle(list)
        return RankFunctions.getRankAfter(lastRank)
    }
//
//    fun getRankBeforeFirst(listId: VaultPath): String {
//        val firstRank = getFirstRankOrMiddle(listId)
//        return RankFunctions.getRankBefore(firstRank)
//    }
//
//    suspend fun getRankAfter(listId: ListId, rank: String): String {
//        val lastRank = getLastRankOrMiddle(listId)
//        return RankFunctions.getRankAfter(lastRank)
//    }
//
//    suspend fun upsertRank(rank: Rank) {
//        database.rankQueries.upsert(rank)
//        messages.saveMessage(NetworkMessage.Type.Update, rank.uuid, EntityType.RANK)
//    }
//
//    suspend fun getRankFor(task: TaskId): String? {
//        return database.rankQueries.getRank(task.uuid).awaitAsOneOrNull()
//    }
//
//    /**
//     * Moves [taskId] to [destId]'s list and places it before or after [destId] depending on the rank.
//     *
//     * @return Whether task changed lists after the reorder.
//     */
//    suspend fun reorderTask(taskId: TaskId, destId: TaskId): Boolean = database.transactionWithResult {
//        val task = getTask(taskId) ?: return@transactionWithResult false
//        val dest = getTask(destId) ?: return@transactionWithResult false
//        val changedLists = task.list != dest.list
//
//        if (changedLists) moveTaskToList(taskId, dest.list)
//        val taskRank = getRankFor(taskId) ?: RankFunctions.firstChar.toString()
//        val destRank = getRankFor(destId) ?: RankFunctions.lastChar.toString()
//
//        if (taskRank == destRank) return@transactionWithResult changedLists
//
//        if (taskRank < destRank) {
//            moveTaskAfter(dest.list, taskId, destRank)
//        } else {
//            moveTaskBefore(dest.list, taskId, destRank)
//        }
//        changedLists
//    }
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
//    suspend fun moveTaskBefore(
//        list: ListId,
//        task: TaskId,
//        destRank: String,
//    ) {
//        val before = database.rankQueries.getRankBefore(list.uuid, destRank)
//            .awaitAsOneOrNull()
//            ?: RankFunctions.firstChar.toString()
//
//        moveTaskBetween(list, task, before, destRank)
//    }
//
//    suspend fun moveTaskAfter(
//        list: ListId,
//        task: TaskId,
//        destRank: String,
//    ) {
//        val after = database.rankQueries.getRankAfter(list.uuid, destRank)
//            .awaitAsOneOrNull()
//            ?: RankFunctions.lastChar.toString()
//
//        moveTaskBetween(list, task, destRank, after)
//    }
//
//    suspend fun moveTaskBetween(
//        list: ListId,
//        task: TaskId,
//        firstRank: String,
//        secondRank: String,
//    ) {
//        val newRank = RankFunctions.getLexicographicMiddle(firstRank, secondRank)
//        upsertRank(Rank(task.uuid, list.uuid, newRank))
//    }
}
