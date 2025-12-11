package me.dvyy.tasks.model.database

import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.dao.SubtaskRelationQueries

class AppQueries {
    val tasks = JsonDataQueries(Task.serializer(), NotesTable)
    val rank = SubtaskRelationQueries(tasks)
}