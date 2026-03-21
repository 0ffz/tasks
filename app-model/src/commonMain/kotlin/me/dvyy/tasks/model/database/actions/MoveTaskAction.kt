package me.dvyy.tasks.model.database.actions

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.actions.Action
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.components.TaskModel
import kotlin.uuid.Uuid

@Serializable
data class MoveTaskAction(
    val task: TaskId,
    val toList: ListId? = null,
    val toTask: TaskId? = null,
) : Action {
    override fun reduce(previous: Action): Action? {
        if (previous !is MoveTaskAction) return null
        if (previous.task == task) return this
        return null
    }
}

@Serializable
data class CreateTaskAction(
    val uuid: Uuid,
    val task: TaskModel,
    val parent: Uuid,
//    val preferRank: String? = null,
    val atEnd: Boolean,
) : Action