package me.dvyy.tasks.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

fun createWorker(): Worker =
    js("""new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))""")

fun createPersistentWorker(): Worker =
    js("""new Worker(new URL("sqlite.persistent.worker.js", import.meta.url))""")

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = WebWorkerDriver(createPersistentWorker())
//        val dirs = AppDirs(Environment.customAppDir ?: "tasks", "dvyy")
//
//        val dataPath = Path(dirs.getUserDataDir())
//        dataPath.createDirectories()
//        val driver: SqlDriver = JdbcSqliteDriver(
//            url = "jdbc:sqlite:${(dataPath / "tasks.db").absolutePathString()}",
//            properties = Properties(),
//            schema = Database.Schema,
//        )
        return driver
    }
}
