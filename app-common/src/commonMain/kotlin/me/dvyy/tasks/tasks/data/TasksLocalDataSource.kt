package me.dvyy.tasks.tasks.data

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.tasks.data.TaskModel.done
import me.dvyy.tasks.tasks.data.TaskModel.tags
import me.dvyy.tasks.tasks.ui.elements.list.TaskUiStateWithPath
import me.dvyy.tasks.tasks.ui.state.TaskUiState

class TasksLocalDataSource(
    val vault: Vault,
) {
    fun moveTask(task: VaultPath, /*from: VaultPath,*/ to: VaultPath) {
        var moveDocumentTo: VaultPath? = null
        vault.update(task, clearOldFrontMatter = false) {
//            val updatedProjects = (it.read<List<String>>("projects") ?: listOf()).minus(from.pathWithoutExt).plus(to.pathWithoutExt)
            val updatedProjects = listOf(to.pathString)
            frontMatter.projects = updatedProjects
        }

        vault.getNote(task)?.let {
            if (it.frontMatter.managed == true) {
                val vaultPath = it.path
                val targetFolder = vault.taskFolderFor(to)
                if (vaultPath.parent != targetFolder) {
                    moveDocumentTo = targetFolder.resolve(vaultPath.displayName + ".md")
                }
            }
        }
//        if (moveDocumentTo != null) vault.moveDocument(task, moveDocumentTo)
    }

    fun colorForTag(tag: String?): Color {
        if (tag == null) return Color.Transparent
        return Color(tag.hashCode().mod(0xFFFFFFFF)).copy(alpha = 1.0f)
    }

    //    val markdownChecklistRegex = "^- \\[[xX ]]".toRegex()
    fun observeListTasks(listId: VaultPath): Flow<List<TaskUiStateWithPath>> {
//        vault.query(KeyHelpers.PATH_KEY eq listId.pathString).project(KeyHelpers.CONTENT_KEY).map {
//            val content = it.single().content()
//            content.lineSequence().filter { it.trim().matches(markdownChecklistRegex) }
//        }
        return vault.getRelations(NoteFrontMatter::projects, listId.pathWithoutExt).map {
            it.map { note ->
                TaskUiStateWithPath(
                    state = TaskUiState(
                        text = note.fileContent ?: "",
                        completed = note.frontMatter.done,
                        highlight = colorForTag(note.frontMatter.tags.firstOrNull()),
                    ),
                    path = note.path
                )
            }
        }
    }
    /*.project( //TODO projection doesn't work for lists?
    documentOf(
        "frontMatter" to documentOf(
            "projects" to null,
            "done" to null,
            "tags" to null,//documentOf()
        ),
        "fileContent" to null,
        "path" to null
    )*/

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
