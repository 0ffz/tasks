package me.dvyy.tasks.data

import ca.gosyer.appdirs.AppDirs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.ui.elements.week.TaskListKey
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
actual class PersistentStore actual constructor() {
    val dirs = AppDirs("tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())

    fun tasksPath(key: TaskListKey) = when (key) {
        is TaskListKey.Date -> dataPath / "dates" / "${key.date}.json"
        is TaskListKey.Named -> dataPath / "projects" / "${key.name}.json"
    }

    actual fun saveList(key: TaskListKey, tasks: List<TaskModel>) {
        val path = tasksPath(key)
        if (tasks.isEmpty()) {
            path.deleteIfExists()
            return
        }
        path.createParentDirectories()
        AppFormats.json.encodeToStream(ListSerializer(TaskModel.serializer()), tasks, path.outputStream())
    }

    actual fun loadTasksForList(key: TaskListKey): Result<List<TaskModel>> {
        val path = tasksPath(key)
        if (!path.exists()) return Result.success(listOf())
        return kotlin.runCatching {
            AppFormats.json.decodeFromStream(ListSerializer(TaskModel.serializer()), path.inputStream())
        }
    }
}
