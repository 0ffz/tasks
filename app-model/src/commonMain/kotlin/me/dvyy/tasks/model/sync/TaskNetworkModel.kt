package me.dvyy.tasks.model.sync

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.TaskModel

@Serializable
class TaskNetworkModel(
    val name: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val list: ListId,
) {
    fun toTaskModel(id: TaskId): TaskModel {
        return TaskModel(
            id = id,
            name = name,
            completed = completed,
            highlight = highlight,
        )
    }
}
