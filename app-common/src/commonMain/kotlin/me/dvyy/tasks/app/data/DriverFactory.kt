package me.dvyy.tasks.app.data

import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.client.mutators.ClientSchema
import me.dvyy.syncengine.client.mutators.asClientSchema
import me.dvyy.tasks.model.database.AppSchema

expect fun createDatabase(): Database

fun createClientDatabase(db: Database): ClientSchema = runBlocking {
    val schema = AppSchema.asClientSchema(db)
    schema.initialize()
    schema
}
