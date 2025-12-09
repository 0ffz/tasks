package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.dao.JsonDataQueries
import me.dvyy.tasks.model.database.dao.SubtaskRelationQueries

class AppQueries(
    private val db: Database,
) {
    val tasks = JsonDataQueries(Task.serializer(), NotesTable)
    val rank = SubtaskRelationQueries(tasks)
}