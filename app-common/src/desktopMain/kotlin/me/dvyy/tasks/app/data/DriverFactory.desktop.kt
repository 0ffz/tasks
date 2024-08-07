package me.dvyy.tasks.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ca.gosyer.appdirs.AppDirs
import me.dvyy.tasks.db.Database
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.div

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dirs = AppDirs(Environment.customAppDir ?: "tasks", "dvyy")

        val dataPath = Path(dirs.getUserDataDir())
        dataPath.createDirectories()
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${(dataPath / "tasks.db").absolutePathString()}",
            properties = Properties(),
            schema = Database.Schema,
        )
        Database.Schema.create(driver)
        return driver
    }
}
