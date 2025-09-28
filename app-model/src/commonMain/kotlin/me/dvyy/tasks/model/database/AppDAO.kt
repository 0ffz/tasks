package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.dao.JsonDataDAO
import me.dvyy.tasks.model.database.dao.SubtaskRelationDAO

class AppDAO(
    val db: Database,
) {
    val tasks = JsonDataDAO(Task.serializer(), NotesTable)
    val rank = SubtaskRelationDAO(tasks)
}