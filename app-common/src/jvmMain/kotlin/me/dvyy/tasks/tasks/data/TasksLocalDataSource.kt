package me.dvyy.tasks.tasks.data

import ca.gosyer.appdirs.AppDirs
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message
import me.dvyy.tasks.model.serializers.AppFormats
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
actual class TasksLocalDataSource actual constructor() {
    val dirs = AppDirs("tasks", "dvyy")
    val dataPath = Path(dirs.getUserDataDir())

    fun tasksPath(key: ListId): Path {
        return when {
            key.isDate -> dataPath / "dates" / "${key.date}.json"
            else -> dataPath / "projects" / "${key.uuid}.json"
        }
    }

    actual fun saveList(key: ListId, list: TaskListModel) {
        val path = tasksPath(key)
        if (list.tasks.isEmpty() && key.isDate) {
            path.deleteIfExists()
            return
        }
        path.createParentDirectories()
//        val filteredTasks = list.tasks.filter { it.name.isNotEmpty() }
        AppFormats.json.encodeToStream(TaskListModel.serializer(), list, path.outputStream())
    }

    actual fun loadTasksForList(key: ListId): Result<TaskListModel?> {
        val path = tasksPath(key)
        if (!path.exists()) return Result.success(null)
        return runCatching {
            AppFormats.json.decodeFromStream(TaskListModel.serializer(), path.inputStream())
        }
    }

    actual fun getProjects(): List<ListId> {
        val path = dataPath / "projects"
        if (!path.exists()) return listOf()
        return path.listDirectoryEntries()
            .map { ListId(uuidFrom(it.nameWithoutExtension)) }
    }

    actual fun deleteList(key: ListId) {
        tasksPath(key).deleteIfExists()
    }

    actual fun saveMessage(type: Message.Type, uuid: EntityId, timestamp: Instant) {
        val path = dataPath / "messages" / "${uuid}.json"
        path.createParentDirectories()
        path.writeText("${type.name} ${timestamp.epochSeconds}") //TODO ktx.serialization
    }
}
