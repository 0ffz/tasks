package me.dvyy.tasks.model.database

import kotlinx.serialization.json.JsonObject
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.components.ProjectModel
import me.dvyy.tasks.model.components.SavedLayoutModel
import me.dvyy.tasks.model.components.TaskModel
import me.dvyy.tasks.model.database.dao.ChildOfQueries
import kotlin.uuid.Uuid

class AppQueries {
    val notes = JsonDataQueries(JsonObject.serializer(), NotesTable)
    val tasks = JsonDataQueries(TaskModel.serializer(), NotesTable)
    val layouts = JsonDataQueries(SavedLayoutModel.serializer(), NotesTable)
    val childOf = ChildOfQueries(ChildOfTable, ChildOfView)
    val projects = Projects(childOf)
//    val rank = SubtaskRelationQueries(tasks)
}

data class ProjectWithId(
    val id: Uuid,
    val title: String,
)

class Projects(
    private val childOf: ChildOfQueries,
) {
    context(tx: Transaction)
    fun getAllSidebar(): List<ProjectWithId> {
        //TODO rewrite as join/create api for ordering by rank
        return childOf.childrenOf(projectRoot).mapNotNull {
            tx.select("SELECT id, data ->> 'title' FROM notes WHERE id = ?", it).firstOrNull {
                ProjectWithId(getUuid(0), getText(1))
            }
        }
    }

    context(tx: Transaction)
    fun getAll(): List<ProjectWithId> {
        //TODO rewrite as join/create api for ordering by rank
        return childOf.childrenOf(projectRoot).mapNotNull {
            tx.select("SELECT id, title FROM projects WHERE id = ?", it).firstOrNull {
                ProjectWithId(getUuid(0), getText(1))
            }
        }
    }

    val crud = JsonDataQueries(ProjectModel.serializer(), NotesTable)

    companion object {
        val projectRoot = Uuid.parse("4fcf8cc9-e53c-496e-b515-a11c3227230d")
    }
}
