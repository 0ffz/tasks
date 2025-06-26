package me.dvyy.tasks.model.schema

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.syncengine.db.Transaction
import me.dvyy.syncengine.db.WriteTransaction
import me.dvyy.syncengine.schema.JsonTable
import org.intellij.lang.annotations.Language
import kotlin.uuid.Uuid

class JsonDAO<T>(
    val serializer: KSerializer<T>,
    val table: JsonTable,
    val json: Json = Json,
) {
    context(tx: Transaction)
    fun childrenOf(uuid: Uuid): List<Uuid> = tx.getList("SELECT id FROM $table WHERE parent = ?", uuid.toString()) {
        Uuid.fromByteArray(getBlob(0))
    }

    context(tx: Transaction)
    fun get(
        id: Uuid,
    ): T = tx.getSingle("SELECT json(data) FROM $table WHERE id = ?", id) {
        json.decodeFromString(serializer, getText(0))
    }

    context(tx: WriteTransaction)
    fun patch(
        id: Uuid,
        data: T,
    ) = patch(id, json.encodeToString(serializer, data))

    context(tx: WriteTransaction)
    fun create(
        id: Uuid,
        data: JsonElement,
    ) = patch(id, data.toString())

    context(tx: WriteTransaction)
    fun delete(id: Uuid) {
        tx.exec("""DELETE FROM $table WHERE id = ?""", id)
    }

    context(tx: WriteTransaction)
    fun patch(
        id: Uuid,
        @Language("JSON") patchString: String,
    ) {
        //INSERT INTO $table(id, data)
        //            VALUES (?, jsonb(?))
        //            ON CONFLICT DO
        tx.exec(
            """
            UPDATE $table SET
            data = jsonb_patch(data, jsonb(?))
            WHERE id = ?
            """.trimIndent(),
            patchString, id
        )
        tx.modified(table)
    }
}

