package me.dvyy.tasks.model.database

import kotlinx.serialization.json.JsonObject
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.components.ProjectModel
import me.dvyy.tasks.model.components.TaskModel
import me.dvyy.tasks.model.database.dao.SubtaskRelationQueries
import kotlin.uuid.Uuid

class AppQueries {
    val notes = JsonDataQueries(JsonObject.serializer(), NotesTable)
    val tasks = JsonDataQueries(TaskModel.serializer(), NotesTable)
    val projects = Projects()
    val rank = SubtaskRelationQueries(tasks)
}

data class ProjectWithId(
    val id: Uuid,
    val title: String,
)

class Projects {
    context(tx: Transaction)
    fun getAll(): List<ProjectWithId> = tx.select("SELECT id, title FROM projects").map {
        ProjectWithId(getUuid(0), getText(1))
    }

    val crud = JsonDataQueries(ProjectModel.serializer(), NotesTable)
}
