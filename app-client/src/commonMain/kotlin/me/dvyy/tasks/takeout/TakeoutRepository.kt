package me.dvyy.tasks.takeout

import co.touchlab.kermit.Logger
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import me.dvyy.syncengine.jsonactions.actions.JsonCreateAction
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.NotesTable
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

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun import(source: Source) {
        Logger.v { "Starting database import" }
        val data = Json.decodeFromSource<Map<Uuid, JsonObject>>(source)
        val nonExisting = database.read {
            data.filter { (notes.get(it.key) == null) }
        }
        Logger.v { "Importing ${nonExisting.size}/${data.size} notes (skipping existing)" }
        nonExisting.forEach {
            database.mutate(
                JsonCreateAction(
                    NotesTable.name,
                    it.key,
                    it.value,
                )
            )
        }
        Logger.v { "Database import complete!" }
    }
}
