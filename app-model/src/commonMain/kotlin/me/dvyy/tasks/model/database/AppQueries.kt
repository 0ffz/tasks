package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.components.Project
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.dao.SubtaskRelationQueries
import kotlin.uuid.Uuid

class AppQueries {
    val tasks = JsonDataQueries(Task.serializer(), NotesTable)
    val projects = Projects()
    val rank = SubtaskRelationQueries(tasks)
}

data class ProjectWithId(
    val id: Uuid,
    val project: Project,
)

class Projects {
    context(tx: Transaction)
    fun getAll(): List<ProjectWithId> = tx.select("SELECT id, title FROM projects").map {
        ProjectWithId(getUuid(0), Project(getText(1)))
    }

    val crud = JsonDataQueries(Project.serializer(), NotesTable)
}