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
import me.dvyy.tasks.tasks.ui.elements.list.TaskUiStateWithPath
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.dizitart.no2.collection.FindOptions
import org.dizitart.no2.common.SortOrder

class TasksLocalDataSource(
    val vault: Vault,
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
            TaskFilters.tasksForList(listId), FindOptions.orderBy(frontMatter("sortOrder"), SortOrder.Ascending)
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

}
