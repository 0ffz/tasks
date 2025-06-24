package me.dvyy.tasks.app.data

import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.runBlocking
import me.dvyy.syncengine.db.Database
import me.dvyy.tasks.model.schema.AppSchema

expect class DriverFactory {
    fun createDriver(): SQLiteDriver
}


fun createClientDatabase(): Database {
    runBlocking {
        Database.write { AppSchema.forEach { it.create() } }
    }
    return Database
}
