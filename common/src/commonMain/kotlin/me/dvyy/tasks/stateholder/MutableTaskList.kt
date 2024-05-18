package me.dvyy.tasks.stateholder

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.data.PersistentStore
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.ui.elements.week.TaskListKey

class MutableTaskList(
    val key: TaskListKey,
    private val localStore: PersistentStore,
    initialTasks: List<TaskModel>
) {
    private val models = initialTasks.toMutableList()
    fun models() = models.toList()

    //    private val states = mutableListOf<TaskWithIDState>()
    private val tasksFlow = MutableStateFlow(models())

    fun tasksFlow(): Flow<List<TaskModel>> = tasksFlow

    private fun emitUpdate() {
        tasksFlow.update { models.toList() }
        localStore.saveList(key, models.toList())
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

//    fun updateIfPresent(uuid: Uuid, state: TaskState) {
//        val index = indexOf(uuid)
//        if (index == -1) return
//        val task = states[index]
//        set(uuid, task.copy(state = state))
//    }

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
}
