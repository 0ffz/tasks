package me.dvyy.tasks.data

import kotlinx.browser.localStorage
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import org.w3c.dom.set

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class PersistentStore {
    actual fun saveDay(date: LocalDate, tasks: List<TaskModel>) {
        val byteArray = AppFormats.cbor.encodeToByteArray(ListSerializer(TaskModel.serializer()), tasks)
        localStorage[date.toString()] = byteArray.toHexString()
    }

    actual fun loadTasksForDay(date: LocalDate): Result<List<TaskModel>> {
        val hexString = localStorage.getItem(date.toString()) ?: return Result.success(emptyList())
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                ListSerializer(TaskModel.serializer()),
                hexString.hexToByteArray()
            )
        }
    }
}
