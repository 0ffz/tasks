package me.dvyy.tasks.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ca.gosyer.appdirs.AppDirs
import me.dvyy.tasks.db.Database
import kotlin.io.path.*

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dirs = AppDirs("tasks", "dvyy")
        val dataPath = Path(dirs.getUserDataDir())
        dataPath.createDirectories()
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${(dataPath / "tasks.db").absolutePathString()}")
        Database.Schema.create(driver)
        return driver
    }
}
