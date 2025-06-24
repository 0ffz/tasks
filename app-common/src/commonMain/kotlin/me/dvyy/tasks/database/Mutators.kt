package me.dvyy.tasks.database

import kotlinx.serialization.json.Json
import me.dvyy.syncengine.db.Transaction
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.db.tables.Table
import me.dvyy.tasks.model.mutators.Mutator

object MutatorsTable : Table(
    """
    CREATE TABLE IF NOT EXISTS mutators(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        data BLOB NOT NULL
    )
    """.trimIndent()
) {
    context(tx: Transaction)
    fun getAllEncoded() = tx.getList("SELECT data FROM mutators") { getBlob(0) }

    context(tx: Transaction)
    inline fun forEachMutator(run: (Mutator) -> Unit) {
        tx.forEach("SELECT json(data) FROM mutators") {
            run(Json.decodeFromString<Mutator>(getText(0)))
        }
    }

    context(tx: WriteTransaction)
    fun append(mutator: Mutator) {
        tx.exec("INSERT INTO mutators(data) VALUES (jsonb(?))", Json.encodeToString(Mutator.serializer(), mutator))
    }
}
