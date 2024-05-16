package me.dvyy.tasks.platforms

import ca.gosyer.appdirs.AppDirs
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.dvyy.tasks.model.AppFormats
import me.dvyy.tasks.model.Task
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
actual class PersistentStore actual constructor() {
    val dirs = AppDirs("tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())

    fun tasksPath(date: LocalDate) = dataPath / "dates" / "$date.json"

    actual fun saveDay(date: LocalDate, tasks: List<Task>) {
        val path = tasksPath(date)
        if (tasks.isEmpty()) {
            path.deleteIfExists()
            return
        }
        path.createParentDirectories()
        AppFormats.json.encodeToStream(ListSerializer(Task.serializer()), tasks, path.outputStream())
    }

    actual fun loadTasksForDay(date: LocalDate): Result<List<Task>> {
        val path = tasksPath(date)
        if (!path.exists()) return Result.success(listOf())
        return kotlin.runCatching {
            AppFormats.json.decodeFromStream(ListSerializer(Task.serializer()), path.inputStream())
        }
    }
}
