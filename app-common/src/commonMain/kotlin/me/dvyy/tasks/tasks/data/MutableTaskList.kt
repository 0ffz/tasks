package me.dvyy.tasks.tasks.data

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.Changelist
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.model.sync.TaskNetworkModel
import me.dvyy.tasks.tasks.ui.elements.list.ListTitle

class MutableTaskList(
    val key: ListKey,
    fromModel: TaskListModel?,
    val queueSave: () -> Unit,
) {
    val customTitle: StateFlow<ListTitle.Project?> get() = _customTitle
    private val _customTitle = MutableStateFlow(fromModel?.title)
    val lastSynced: StateFlow<Instant?> get() = _lastSynced
    private val _lastSynced = MutableStateFlow(fromModel?.lastSynced)

    private val models = fromModel?.tasks?.toMutableList() ?: mutableListOf()
    private val tasksFlow = MutableStateFlow(models())

    fun toListModel() = TaskListModel(customTitle.value, models.toList(), _lastSynced.value)
    fun models() = models.toList()

    fun tasksFlow(): Flow<List<TaskModel>> = tasksFlow

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

    fun setCustomTitle(title: ListTitle.Project?) {
        _customTitle.value = title
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
