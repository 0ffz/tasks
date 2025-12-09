package me.dvyy.tasks.model.database

import me.dvyy.syncengine.actions.Actions
import me.dvyy.tasks.model.database.actions.JsonActions
import me.dvyy.tasks.model.database.dao.JsonDataDAO

class AppActions(
    val appDAO: AppDAO,
    val actions: Actions,
) {
    val tasks = jsonActions(appDAO.tasks)

    private fun <T> jsonActions(dao: JsonDataDAO<T>) = JsonActions(appDAO.db, dao, actions)
}