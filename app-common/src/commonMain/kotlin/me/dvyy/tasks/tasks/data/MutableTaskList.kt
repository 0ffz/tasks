package me.dvyy.tasks.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.model.*
import me.dvyy.tasks.model.sync.TaskNetworkModel

class MutableTaskList(
    val key: ListId,
    fromModel: TaskListModel,
    val queueSave: () -> Unit,
) {
    val _properties = MutableStateFlow(fromModel.properties)

    val properties = _properties.asStateFlow()

    private val models = fromModel.tasks.toMutableList()
    private val tasksFlow = MutableStateFlow(models())

    fun toListModel() = TaskListModel(_properties.value, models.toList())
    fun models() = models.toList()

    fun tasksFlow(): Flow<List<TaskModel>> = tasksFlow

    operator fun get(taskId: TaskId): TaskModel? {
        return models.firstOrNull { it.id == taskId }
    }

    operator fun set(uuid: TaskId, model: TaskModel) {
        val index = indexOf(uuid)
        if (index == -1) {
            models.add(model)
        } else {
            models[index] = model
        }
        emitUpdate()
    }

    fun remove(uuid: TaskId): TaskModel? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.removeAt(index).also {
            emitUpdate()
        }
    }

    fun indexOf(taskId: TaskId) = models.indexOfFirst { it.id == taskId }

    fun taskAfter(uuid: TaskId): TaskId? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.getOrNull(indexOf(uuid) + 1)?.id
    }

    fun taskBefore(uuid: TaskId): TaskId? {
        val index = indexOf(uuid)
        if (index == -1) return null
        return models.getOrNull(indexOf(uuid) - 1)?.id
    }

    fun reorder(from: TaskId, to: TaskId) {
        val fromIndex = indexOf(from)
        val toIndex = indexOf(to)
        if (fromIndex == -1 || toIndex == -1) return
        val task = models.removeAt(fromIndex)
        models.add(toIndex, task)
        emitUpdate()
    }

    fun setProperties(props: TaskListProperties) {
        _properties.update { props }
        queueSave()
    }

    fun changesSinceLastSync(): Changelist<TaskNetworkModel> {
        TODO()
//        val sync = _lastSynced.value ?: return models.toList()
//        return models.filter { it.modified > sync }
    }

    private fun emitUpdate() {
        tasksFlow.update { models.toList() }
        queueSave()
    }
}
