package me.dvyy.tasks.model.database.actions

import kotlinx.serialization.Serializable
import me.dvyy.syncengine.actions.Action
import me.dvyy.tasks.model.components.TaskModel
import kotlin.uuid.Uuid

@Serializable
data class MoveChildAction(
    val item: Uuid,
    val toParent: Uuid? = null,
    val atChild: Uuid? = null,
    val preferredRank: String? = null,
    val atEnd: Boolean? = null,
) : Action {
    override fun reduce(previous: Action): Action? {
        if (previous !is MoveChildAction) return null
        if (previous.item == item) return this
        return null
    }
}

@Serializable
data class CreateTaskAction(
    val uuid: Uuid,
    val task: TaskModel,
    val parent: Uuid,
//    val preferRank: String? = null,
    val atEnd: Boolean? = null,
) : Action