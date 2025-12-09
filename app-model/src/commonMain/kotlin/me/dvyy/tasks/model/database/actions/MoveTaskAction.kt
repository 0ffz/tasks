package me.dvyy.tasks.model.database.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.dvyy.syncengine.actions.Action
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId

@Serializable
@SerialName("move-task")
data class MoveTaskAction(
    val task: TaskId,
    val toList: ListId,
) : Action {
    override fun reduce(previous: Action): Action? {
        if (previous !is MoveTaskAction) return null
        if (previous.task == task) return this
        return null
    }
}
