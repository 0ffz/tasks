package me.dvyy.tasks.tasks.data

import Uuid
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.Message
import me.dvyy.tasks.model.serializers.AppFormats

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class TasksLocalDataSource {
    private val settings = Settings()

    actual fun saveList(listId: ListId, list: TaskListModel) {
        val byteArray = AppFormats.cbor.encodeToByteArray(TaskListModel.serializer(), list)
        settings[listId.uniqueIdentifier] = byteArray.toHexString()
    }

    actual fun loadTasksForList(listId: ListId): Result<TaskListModel?> {
        val hexString: String = settings[listId.uniqueIdentifier] ?: return Result.success(null)
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                TaskListModel.serializer(),
                hexString.hexToByteArray()
            )
        }
    }

    actual fun observeProjects(): List<ListId> {
        return settings.keys
            .filter { it.startsWith("project/") }
            .mapNotNull {
                runCatching { Uuid.Project.fromIdentifier(it) }.getOrNull()
            }
    }

    actual fun deleteList(listId: ListId) {
        settings.remove(listId.uniqueIdentifier)
    }

    actual fun saveMessage(
        type: Message.Type,
        uuid: EntityId,
        timestamp: Instant,
    ) {
    }
}
