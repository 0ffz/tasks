package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.jsonactions.JsonActions
import me.dvyy.syncengine.jsonactions.JsonDataQueries

class AppActions(
    private val db: Database,
    private val appQueries: AppQueries,
    private val actions: Actions,
) {
    val tasks = jsonActions(appQueries.tasks)

    private fun <T> jsonActions(dao: JsonDataQueries<T>) = JsonActions(db, dao, actions)
}