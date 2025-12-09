package me.dvyy.tasks.model.database

import kotlinx.coroutines.flow.Flow
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Transaction
import me.dvyy.syncengine.actions.Action
import me.dvyy.syncengine.actions.Actions

class AppDatabase(
    @PublishedApi
    internal val db: Database,
    private val actions: Actions,
    val query: AppQueries,
    val mutate: AppActions,
) {
    suspend inline fun <T> read(
        crossinline block: context(Transaction) AppQueries.() -> T,
    ): T = db.read { query.block() }

    inline fun <T> watch(
        vararg tables: String,
        crossinline read: context(Transaction) AppQueries.() -> T,
    ): Flow<T> = db.watch(*tables) { query.read() }

    suspend fun mutate(mutator: Action) {
        actions.invoke(mutator)
    }
}
