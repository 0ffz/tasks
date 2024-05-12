package me.dvyy.tasks.platforms

import kotlinx.browser.localStorage
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import me.dvyy.tasks.model.AppFormats
import me.dvyy.tasks.model.Task
import org.w3c.dom.set

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class PersistentStore {
    actual fun saveDay(date: LocalDate, tasks: List<Task>) {
        val byteArray = AppFormats.cbor.encodeToByteArray(ListSerializer(Task.serializer()), tasks)
        localStorage[date.toString()] = byteArray.toHexString()
    }

    actual fun loadTasksForDay(date: LocalDate): Result<List<Task>> {
        val hexString = localStorage.getItem(date.toString()) ?: return Result.success(emptyList())
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                ListSerializer(Task.serializer()),
                hexString.hexToByteArray()
            )
        }
    }
}
