package me.dvyy.tasks.tasks.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.serializers.AppFormats

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class TasksLocalDataSource {
    private val settings = Settings()

    actual fun saveList(key: ListKey, list: TaskListModel) {
        val byteArray = AppFormats.cbor.encodeToByteArray(TaskListModel.serializer(), list)
        settings[key.uniqueIdentifier] = byteArray.toHexString()
    }

    actual fun loadTasksForList(key: ListKey): Result<TaskListModel?> {
        val hexString: String = settings[key.uniqueIdentifier] ?: return Result.success(null)
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                TaskListModel.serializer(),
                hexString.hexToByteArray()
            )
        }
    }

    actual fun getProjects(): List<ListKey.Project> {
        return settings.keys
            .filter { it.startsWith("project/") }
            .mapNotNull {
                runCatching { ListKey.Project.fromIdentifier(it) }.getOrNull()
            }
    }

    actual fun deleteList(key: ListKey) {
        settings.remove(key.uniqueIdentifier)
    }
}
