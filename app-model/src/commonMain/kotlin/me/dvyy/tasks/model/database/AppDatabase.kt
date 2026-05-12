package me.dvyy.tasks.model.database

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Transaction
import me.dvyy.syncengine.actions.Action
import me.dvyy.syncengine.actions.Actions
import me.dvyy.syncengine.jsonactions.actions.JsonCreateAction
import kotlin.uuid.Uuid

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

    suspend fun import(uuid: Uuid, item: ExportedTask) {
        // Create 'notes' entry
        mutate(JsonCreateAction(NotesTable.name, uuid, item.notes))

        // Insert childOf at preferred rank
        item.childOf.forEach { (parentUuid, preferredRank) ->
            mutate.childOf.move(uuid, parentUuid, preferredRank = preferredRank)
        }

        if (item.notes["type"]?.jsonPrimitive?.content == "project") {
            mutate.childOf.move(uuid, toParent = Projects.projectRoot)
        }
    }

    fun launchMutate(mutator: Action) {
        actions.invokeAsync(mutator)
    }
}

@Serializable
data class ExportedTask(
    val notes: JsonObject,
    val childOf: Map<Uuid, String>,
)