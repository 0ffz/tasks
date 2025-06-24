package me.dvyy.tasks.app.data

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import ca.gosyer.appdirs.AppDirs
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

actual class DriverFactory {
    actual fun createDriver(): SQLiteDriver {
        val dirs = AppDirs(Environment.customAppDir ?: "tasks", "dvyy")

        val dataPath = Path(dirs.getUserDataDir())
        dataPath.createDirectories()
//        val driver: SqlDriver = JdbcSqliteDriver(
//            url = "jdbc:sqlite:${(dataPath / "tasks.db").absolutePathString()}",
//            properties = Properties(),
//            schema = Database.Schema.synchronous(),
//        )
        return BundledSQLiteDriver()
    }
}
