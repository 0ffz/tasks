package me.dvyy.tasks.takeout

import co.touchlab.kermit.Logger
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.statement.getUuid
import me.dvyy.tasks.model.database.AppDatabase
import kotlin.uuid.Uuid

class TakeoutRepository(
    val database: AppDatabase,
) {
    suspend fun export(sink: Sink) {
        Logger.v { "Starting database export" }
        val notes = database.read {
            buildMap {
                notes.forEach { id, json ->
                    put(id, json)
                }
            }
        }
        Json.encodeToSink<Map<Uuid, JsonObject>>(notes, sink)
        Logger.v { "Database export complete!" }
    }

    suspend fun migrateOldDb(databaseFilePath: String, sink: Sink) {
        val notes = buildJsonObject {
            Database(databaseFilePath).use {
                it.read {
                    select("SELECT task.uuid, text, highlight, completed, list, rank FROM task JOIN rank ON task.uuid = rank.uuid").forEach {
                        putJsonObject(getUuid(0).toHexDashString()) {
                            put("text", getText(1))
                            put("highlight", getText(2))
                            put("done", getBoolean(3))
                            put("parent", getUuid(4).toHexDashString())
                            put("rank", getText(5))
                        }
                    }
                    select("SELECT uuid, title FROM taskList WHERE isProject = 1").forEach {
                        putJsonObject(getUuid(0).toHexDashString()) {
                            put("title", getText(1))
                            put("type", "project")
                        }
                    }
                }
            }
        }
        Logger.i { "Migration complete, writing to file..." }
        Json.encodeToSink(notes, sink)

    }
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun import(source: Source, onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }) {
        Logger.v { "Starting database import" }
        val data = Json.decodeFromSource<Map<Uuid, JsonElement>>(source)
        val nonExisting = database.read {
            data.filter { (notes.get(it.key) == null) }
        }
        Logger.v { "Importing ${nonExisting.size}/${data.size} notes (skipping existing)" }
        nonExisting.entries.forEachIndexed { index, entry ->
            val jsonObject = entry.value as? JsonObject ?: return@forEachIndexed
            database.import(entry.key, jsonObject)
            onProgress(index + 1, nonExisting.size)
        }
        Logger.v { "Database import complete!" }
    }
}
