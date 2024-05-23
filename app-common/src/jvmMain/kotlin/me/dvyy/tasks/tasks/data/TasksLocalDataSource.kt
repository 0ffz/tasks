package me.dvyy.tasks.tasks.data

import ca.gosyer.appdirs.AppDirs
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.serializers.AppFormats
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor() {
    val dirs = AppDirs("tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())

    fun tasksPath(key: ListKey): Path {
        return when (key) {
            is ListKey.Date -> dataPath / "dates" / "${key.date}.json"
            is ListKey.Project -> dataPath / "projects" / "${key.uuid}.json"
        }
    }

    actual fun saveList(key: ListKey, list: TaskListModel) {
        val path = tasksPath(key)
        if (list.tasks.isEmpty() && key is ListKey.Date) {
            path.deleteIfExists()
            return
        }
        path.createParentDirectories()
//        val filteredTasks = list.tasks.filter { it.name.isNotEmpty() }
        AppFormats.json.encodeToStream(TaskListModel.serializer(), list, path.outputStream())
    }

    actual fun loadTasksForList(key: ListKey): Result<TaskListModel?> {
        val path = tasksPath(key)
        if (!path.exists()) return Result.success(null)
        return runCatching {
            AppFormats.json.decodeFromStream(TaskListModel.serializer(), path.inputStream())
        }
    }

    actual fun getProjects(): List<ListKey.Project> {
        val path = dataPath / "projects"
        if (!path.exists()) return listOf()
        return path.listDirectoryEntries()
            .map { ListKey.Project(uuidFrom(it.nameWithoutExtension)) }
    }

    actual fun deleteList(key: ListKey) {
        tasksPath(key).deleteIfExists()
    }
}
