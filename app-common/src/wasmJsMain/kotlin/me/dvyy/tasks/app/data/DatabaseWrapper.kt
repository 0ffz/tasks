package me.dvyy.tasks.app.data

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.async.coroutines.awaitQuery
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import me.dvyy.tasks.db.client.Database

class DatabaseWrapper(private val driver: WebWorkerDriver) {
    private var database: Database? = null

    suspend fun initializeDatabase(): Database {
        if (database != null) return database!!

        database = createClientDatabase(driver).also { migrateIfNeeded() }
        return database!!
    }

    private suspend fun migrateIfNeeded() {
        val mapper = { cursor: SqlCursor ->
            QueryResult.Value(if (cursor.next().value) cursor.getLong(0) else null)
        }
        val oldVersion = driver.awaitQuery(null, "PRAGMA $VERSION_PRAGMA", mapper, 0, null).value ?: 0L

        val newVersion = Database.Schema.version

        if (oldVersion == 0L) {
            Database.Schema.awaitCreate(driver)
            driver.await(null, "PRAGMA $VERSION_PRAGMA=$newVersion", 0)
        } else if (oldVersion < newVersion) {
            Database.Schema.awaitMigrate(driver, oldVersion, newVersion)
            driver.await(null, "PRAGMA $VERSION_PRAGMA=$newVersion", 0)
        }
    }

    companion object {
        fun create() = DatabaseWrapper(DriverFactory().createDriver() as WebWorkerDriver)
        private const val VERSION_PRAGMA = "user_version"
    }
}
