package me.dvyy.tasks.model.schema

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.dvyy.syncengine.db.Transaction
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.db.bindUuid
import me.dvyy.syncengine.db.tables.TableReading
import org.intellij.lang.annotations.Language
import java.util.*
import kotlin.uuid.Uuid

class NotesDAO<T>(
    val serializer: KSerializer<T>,
    val table: TableReading,
    val json: Json = Json,
) {
    context(tx: Transaction)
    fun get(
        id: Uuid,
    ): T = tx.getSingle("SELECT json(data) FROM $table WHERE id = ?", id) {
        json.decodeFromString(serializer, getText(0))
    }

    context(tx: WriteTransaction)
    fun mutate(
        id: Uuid,
        data: T,
    ) = mutate(id, json.encodeToString(serializer, data))

    context(tx: WriteTransaction)
    fun delete(id: Uuid) {
        tx.exec("""DELETE FROM $table WHERE id = ?""", id)
    }

    context(tx: WriteTransaction)
    fun mutate(
        id: Uuid,
        @Language("JSON") patchString: String,
    ) {
        tx.exec(
            """
            INSERT INTO $table(id, data)
            VALUES (?, jsonb(?)) 
            ON CONFLICT DO UPDATE SET 
            data = jsonb_patch(data, jsonb(excluded.data))
            """.trimIndent(),
            id, patchString.toString()
        )
        tx.modified(table)
    }
}
