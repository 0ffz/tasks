package me.dvyy.tasks.model.database

import me.dvyy.syncengine.schema.Mutators
import me.dvyy.tasks.model.database.dao.JsonDataDAO
import me.dvyy.tasks.model.database.helpers.JsonMutators
import me.dvyy.tasks.model.database.mutators.Mutator

class AppMutators(
    val appDAO: AppDAO,
    val mutators: Mutators<Mutator>,
) {
    val tasks = mutate(appDAO.tasks)

    private fun <T> mutate(dao: JsonDataDAO<T>) = JsonMutators(appDAO.db, dao, mutators)
}