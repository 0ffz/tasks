package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.jsonactions.JsonActions
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.model.components.TaskModel
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveTaskAction
import kotlin.uuid.Uuid

class AppActions(
    private val db: Database,
    private val appQueries: AppQueries,
    private val actions: Actions,
) {
    val tasks = TaskActions(actions, appQueries, db)
    val projects = jsonActions(appQueries.projects.crud)
    val childOf = jsonActions(appQueries.projects.crud)

    private fun <T> jsonActions(dao: JsonDataQueries<T>) = JsonActions(db, dao, actions)
}

class TaskActions(
    private val actions: Actions,
    private val appQueries: AppQueries,
    private val db: Database,
) {
    val json = JsonActions(db, appQueries.tasks, actions)
    suspend fun create(task: TaskModel, parent: Uuid, atEnd: Boolean = true) {
        actions.invoke(CreateTaskAction(Uuid.random(), task, parent, atEnd))
    }

    suspend fun move(task: Uuid, toList: ListId) {
        actions(MoveTaskAction(task.asTask(), toList))
    }

    suspend fun update(id: Uuid, new: TaskModel) {
        json.patch(id, new)
    }

    suspend fun delete(id: Uuid) {
        json.delete(id)
    }
}