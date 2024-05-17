package me.dvyy.tasks.stateholder

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.elements.week.TaskWithIDState

class MutableTaskList {
    private val taskIndices = mutableMapOf<Uuid, Int>()
    private val tasksInOrder = mutableListOf<TaskWithIDState>()

    private val tasksFlow = MutableStateFlow<List<TaskWithIDState>?>(null)
    fun tasksFlow(): StateFlow<List<TaskWithIDState>?> = tasksFlow

    private fun emitUpdate() = tasksFlow.update { tasksInOrder.toList() }

    operator fun set(uuid: Uuid, task: TaskWithIDState) {
        val index = taskIndices[uuid]
        if (index == null) {
            taskIndices[uuid] = tasksInOrder.size
            tasksInOrder.add(task)
        } else {
            tasksInOrder[index] = task
        }
        emitUpdate()
    }

    fun update(uuid: Uuid, state: TaskState) {
        val index = taskIndices[uuid] ?: error("Task not found")
        val task = tasksInOrder[index]
        set(uuid, task.copy(state = state))
    }

    fun remove(uuid: Uuid) {
        val index = taskIndices[uuid] ?: return
        taskIndices.remove(uuid)
        tasksInOrder.removeAt(index)
        taskIndices.forEach { (key, value) ->
            if (value > index) taskIndices[key] = value - 1
        }
        emitUpdate()
    }

    fun taskAfter(uuid: Uuid): Uuid? {
        val index = taskIndices[uuid] ?: return null
        return if (index + 1 < tasksInOrder.size) tasksInOrder[index + 1].uuid else null
    }
}
