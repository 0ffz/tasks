package me.dvyy.tasks.data

import kotlinx.browser.localStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.ui.elements.week.TaskListKey
import org.w3c.dom.set

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class PersistentStore {
    actual fun saveList(key: TaskListKey, tasks: List<TaskModel>) {
        val byteArray = AppFormats.cbor.encodeToByteArray(ListSerializer(TaskModel.serializer()), tasks)
        localStorage[key.toString()] = byteArray.toHexString()
    }

    actual fun loadTasksForList(key: TaskListKey): Result<List<TaskModel>> {
        val hexString = localStorage.getItem(key.toString()) ?: return Result.success(emptyList())
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                ListSerializer(TaskModel.serializer()),
                hexString.hexToByteArray()
            )
        }
    }
}
