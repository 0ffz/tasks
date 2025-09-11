package me.dvyy.tasks.app.data

import ca.gosyer.appdirs.AppDirs
import me.dvyy.sqlite.Database
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.div

actual fun createDatabase(): Database {
    val dirs = AppDirs(Environment.customAppDir ?: "tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())
    dataPath.createDirectories()
    //TODO swap back to tasks.db once migrations figured out
    return Database((dataPath / "database.db").absolutePathString())
}