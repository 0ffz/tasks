package me.dvyy.tasks.tasks.data

import ca.gosyer.appdirs.AppDirs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.ui.elements.list.TaskListKey
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor() {
    val dirs = AppDirs("tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())

    fun tasksPath(key: TaskListKey) = when (key) {
        is TaskListKey.Date -> dataPath / "dates" / "${key.date}.json"
        is TaskListKey.Project -> dataPath / "projects" / "${key.name}.json"
    }

    actual fun saveList(key: TaskListKey, tasks: List<TaskModel>) {
        val path = tasksPath(key)
        if (tasks.isEmpty() && key is TaskListKey.Date) {
            path.deleteIfExists()
            return
        }
        path.createParentDirectories()
        val filteredTasks = tasks.filter { it.name.isNotEmpty() }
        AppFormats.json.encodeToStream(ListSerializer(TaskModel.serializer()), filteredTasks, path.outputStream())
    }

    actual fun loadTasksForList(key: TaskListKey): Result<List<TaskModel>> {
        val path = tasksPath(key)
        if (!path.exists()) return Result.success(listOf())
        return runCatching {
            AppFormats.json.decodeFromStream(ListSerializer(TaskModel.serializer()), path.inputStream())
                .filter { it.name.isNotEmpty() }
        }
    }

    actual fun getProjects(): Result<List<TaskListKey.Project>> {
        val path = dataPath / "projects"
        if (!path.exists()) return Result.success(listOf())
        return runCatching {
            path.listDirectoryEntries()
                .map { TaskListKey.Project(it.nameWithoutExtension) }
        }
    }
}
