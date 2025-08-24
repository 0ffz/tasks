package me.dvyy.tasks.app.data

import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.client.mutators.ClientSchema
import me.dvyy.syncengine.client.mutators.asClientSchema
import me.dvyy.tasks.model.schema.AppSchema

expect class DriverFactory {
    fun createDriver(): SQLiteDriver
}

fun createClientDatabase(db: Database): ClientSchema = runBlocking {
    val schema = AppSchema
        .asClientSchema(db)
    schema.initialize()
    schema
}
