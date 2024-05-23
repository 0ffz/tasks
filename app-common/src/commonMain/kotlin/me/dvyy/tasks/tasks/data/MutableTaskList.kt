package me.dvyy.tasks.tasks.data

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle

class MutableTaskList(
    val key: ListKey,
    val title: ListTitle.Project?,
    initialTasks: List<TaskModel>
) {
    private val models = initialTasks.toMutableList()
    fun toListModel() = TaskListModel(title, models.toList())
    fun models() = models.toList()

    private val tasksFlow = MutableStateFlow(models())

    fun tasksFlow(): Flow<List<TaskModel>> = tasksFlow

    private fun emitUpdate() {
        tasksFlow.update { models.toList() }
    }

    operator fun get(uuid: Uuid): TaskModel? {
        return models.firstOrNull { it.uuid == uuid }
    }

    operator fun set(uuid: Uuid, model: TaskModel) {
        val index = indexOf(uuid)
        if (index == -1) {
            models.add(model)
        } else {
            models[index] = model
        }
        emitUpdate()
    }

    fun remove(uuid: Uuid): TaskModel? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.removeAt(index).also {
            emitUpdate()
        }
    }

    fun indexOf(uuid: Uuid) = models.indexOfFirst { it.uuid == uuid }

    fun taskAfter(uuid: Uuid): Uuid? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.getOrNull(indexOf(uuid) + 1)?.uuid
    }

    fun taskBefore(uuid: Uuid): Uuid? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.getOrNull(indexOf(uuid) - 1)?.uuid
    }

    fun reorder(from: Uuid, to: Uuid) {
        val fromIndex = indexOf(from)
        val toIndex = indexOf(to)
        if (fromIndex == -1 || toIndex == -1) return
        val task = models.removeAt(fromIndex)
        models.add(toIndex, task)
        emitUpdate()
    }
}
