package me.dvyy.tasks.platforms

import kotlinx.browser.localStorage
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import me.dvyy.tasks.serialization.AppFormats
import me.dvyy.tasks.serialization.Task
import org.w3c.dom.set

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class PersistentStore {
    actual fun saveDay(date: LocalDate, tasks: List<Task>) {
        val byteArray = AppFormats.cbor.encodeToByteArray(ListSerializer(Task.serializer()), tasks)
        localStorage[date.toString()] = byteArray.toHexString()
    }

    actual fun loadTasksForDay(date: LocalDate): List<Task> {
        val hexString = localStorage.getItem(date.toString()) ?: return emptyList()
        return AppFormats.cbor.decodeFromByteArray(
            ListSerializer(Task.serializer()),
            hexString.hexToByteArray()
        )
    }
}
