package me.dvyy.tasks.app.data

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import me.dvyy.tasks.db.client.Database

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(schema = Database.Schema.synchronous(), context, "tasks.db")
        return driver
    }
}
