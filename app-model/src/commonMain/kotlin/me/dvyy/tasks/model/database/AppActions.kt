package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.jsonactions.JsonActions
import me.dvyy.syncengine.jsonactions.JsonDataQueries
import me.dvyy.syncengine.jsonactions.actions.DeleteEntityAction
import me.dvyy.tasks.model.components.TaskModel
import me.dvyy.tasks.model.database.actions.CreateTaskAction
import me.dvyy.tasks.model.database.actions.MoveChildAction
import kotlin.uuid.Uuid

class AppActions(
    private val db: Database,
    private val appQueries: AppQueries,
    private val actions: Actions,
) {
    val tasks = TaskActions(actions, appQueries, db)
    val projects = jsonActions(appQueries.projects.crud)
    val layouts = jsonActions(appQueries.layouts)
    val childOf = ChildActions(actions, appQueries, db)


    suspend fun delete(id: Uuid) {
        actions.invoke(DeleteEntityAction(id))
    }

    private fun <T> jsonActions(dao: JsonDataQueries<T>) = JsonActions(db, dao, actions)
}

class ChildActions(
    private val actions: Actions,
    private val appQueries: AppQueries,
    private val db: Database,
) {
    suspend fun move(
        item: Uuid,
        toParent: Uuid,
        atChild: Uuid? = null,
        preferredRank: String? = null,
        atEnd: Boolean? = null,
    ) {
        // Don't invoke action if moving to the same position
        val currentParent = db.read { appQueries.childOf.getRankFor(item)?.parent }
        if (currentParent == toParent && atChild == null || atChild == item) return
        actions.invoke(
            MoveChildAction(item, toParent, atChild, preferredRank, atEnd)
        )
    }
}

class TaskActions(
    private val actions: Actions,
    private val appQueries: AppQueries,
    private val db: Database,
) {
    val json = JsonActions(db, appQueries.tasks, actions)
    suspend fun create(task: TaskModel, parent: Uuid, atEnd: Boolean = true): Uuid {
        val id = Uuid.generateV7()
        actions.invoke(CreateTaskAction(id, task, parent, atEnd))
        return id
    }

    suspend fun update(id: Uuid, new: TaskModel) {
        json.patch(id, new)
    }

    suspend fun delete(id: Uuid) {
        actions.invoke(DeleteEntityAction(id))
    }
}