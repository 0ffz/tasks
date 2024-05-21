package me.dvyy.tasks.tasks.data

import kotlinx.browser.localStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.ui.elements.list.TaskListKey
import org.w3c.dom.set

@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
actual class TasksLocalDataSource {
    actual fun saveList(key: TaskListKey, tasks: List<TaskModel>) {
        val byteArray = AppFormats.cbor.encodeToByteArray(ListSerializer(TaskModel.serializer()), tasks)
        localStorage[key.toString()] = byteArray.toHexString()
        if (key is TaskListKey.Project) {
            val projects = getProjects().getOrNull().orEmpty().toMutableList()
            if (key !in projects) {
                projects.add(key)
                localStorage["projects"] = AppFormats.cbor.encodeToByteArray(
                    ListSerializer(TaskListKey.Project.serializer()),
                    projects
                ).toHexString()
            }
        }
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

    actual fun getProjects(): Result<List<TaskListKey.Project>> {
        val projects = localStorage.getItem("projects") ?: return Result.success(emptyList())
        return runCatching {
            AppFormats.cbor.decodeFromByteArray(
                ListSerializer(TaskListKey.Project.serializer()),
                projects.hexToByteArray()
            )
        }
    }
}
