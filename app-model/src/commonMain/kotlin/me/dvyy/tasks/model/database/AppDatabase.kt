package me.dvyy.tasks.model.database

import me.dvyy.syncengine.actions.Action

class AppDatabase(
    val query: AppDAO,
    val mutate: AppActions,
) {
    suspend fun mutate(mutator: Action) {
        mutate.actions.invoke(mutator)
    }
}
