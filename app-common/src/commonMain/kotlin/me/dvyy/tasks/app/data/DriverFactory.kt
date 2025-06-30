package me.dvyy.tasks.app.data

import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.runBlocking
import me.dvyy.syncengine.client.mutators.MutatorsTable
import me.dvyy.syncengine.client.mutators.RollbackJsonTable
import me.dvyy.syncengine.client.mutators.asClientSchema
import me.dvyy.syncengine.db.Database
import me.dvyy.syncengine.schema.Schema
import me.dvyy.tasks.model.schema.AppSchema

expect class DriverFactory {
    fun createDriver(): SQLiteDriver
}

fun createClientDatabase() {
    runBlocking {
        AppSchema.asClientSchema().initialize()
        Database.write {
            MutatorsTable.create()
        }
    }
}
