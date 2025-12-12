package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.jsonactions.JsonActions
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import kotlin.uuid.Uuid

class AppActions(
    private val db: Database,
    private val appQueries: AppQueries,
    private val actions: Actions,
) {
    val tasks = TaskActions(actions, appQueries, db)

    private fun <T> jsonActions(dao: JsonDataQueries<T>) = JsonActions(db, dao, actions)
}

class TaskActions(
    private val actions: Actions,
    private val appQueries: AppQueries,
    private val db: Database,
) {
    val json = JsonActions(db, appQueries.tasks, actions)
    suspend fun create(task: Task) {
        actions.invoke(CreateTaskAction(task))
    }

    suspend fun update(id: Uuid, new: Task) {
        json.patch(id, new)
    }

    suspend fun delete(id: Uuid) {
        json.delete(id)
    }
}