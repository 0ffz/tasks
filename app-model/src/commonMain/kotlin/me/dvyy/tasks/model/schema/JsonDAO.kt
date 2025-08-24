package me.dvyy.tasks.model.schema

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.syncengine.schema.JsonTable
import org.intellij.lang.annotations.Language
import kotlin.uuid.Uuid

class JsonDAO<T>(
    val serializer: KSerializer<T>,
    val table: JsonTable,
    val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    },
) {
    context(tx: Transaction)
    fun get(
        id: Uuid,
    ): T? = tx.getOrNull("SELECT json(data) FROM $table WHERE id = ?", id) {
        json.decodeFromString(serializer, getText(0))
    }

    context(tx: Transaction)
    fun getJsonElement(id: Uuid) = tx.getSingle("SELECT json(data) FROM $table WHERE id = ?", id) {
        json.parseToJsonElement(getText(0))
    }

    context(tx: WriteTransaction)
    fun create(
        id: Uuid,
        data: JsonElement,
    ) {
        tx.exec("INSERT INTO $table (id, data, owner) VALUES (?, jsonb(?), ?)", id, data.toString(), tx.identity)
        tx.modified(table)
    }

    context(tx: WriteTransaction)
    fun patch(
        id: Uuid,
        data: T,
    ) = patch(id, json.encodeToString(serializer, data))

    context(tx: WriteTransaction)
    fun delete(id: Uuid) {
        tx.exec("DELETE FROM $table WHERE id = ? AND owner = ?", id, tx.identity)
        tx.modified(table)
    }

    context(tx: WriteTransaction)
    fun jsonSet(id: Uuid, path: String, value: String) {
        tx.exec(
            "UPDATE $table SET data = jsonb_set(data, ?, jsonb(?)) WHERE id = ? AND owner = ?",
            path, value, id, tx.identity
        )
        tx.modified(table)
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
            WHERE id = ? AND owner = ?
            """.trimIndent(),
            patchString, id, tx.identity
        )
        tx.modified(table)
    }
}

