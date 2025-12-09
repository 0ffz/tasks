package me.dvyy.tasks.model.database

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.actions.Actions
import me.dvyy.tasks.model.database.actions.JsonActions
import me.dvyy.tasks.model.database.dao.JsonDataQueries

class AppActions(
    private val db: Database,
    private val appQueries: AppQueries,
    private val actions: Actions,
) {
    val tasks = jsonActions(appQueries.tasks)

    private fun <T> jsonActions(dao: JsonDataQueries<T>) = JsonActions(db, dao, actions)
}