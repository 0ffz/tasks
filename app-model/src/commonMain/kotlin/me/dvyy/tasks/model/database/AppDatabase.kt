package me.dvyy.tasks.model.database

import me.dvyy.tasks.model.database.mutators.Mutator

class AppDatabase(
    val query: AppDAO,
    val mutate: AppMutators,
) {
    suspend fun mutate(mutator: Mutator) {
        mutate.mutators.invoke(mutator)
    }
}
