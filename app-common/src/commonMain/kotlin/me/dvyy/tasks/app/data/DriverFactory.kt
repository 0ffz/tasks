package me.dvyy.tasks.app.data

import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.runBlocking
import me.dvyy.syncengine.db.Database
import me.dvyy.syncengine.schema.MutatorsTable
import me.dvyy.syncengine.schema.createSchema
import me.dvyy.tasks.model.schema.NotesTable
import me.dvyy.tasks.model.schema.SubtaskTable

expect class DriverFactory {
    fun createDriver(): SQLiteDriver
}

fun createClientDatabase() {
    runBlocking {
        Database.write {
            createSchema(NotesTable, SubtaskTable)
            MutatorsTable.create()
        }
    }
}