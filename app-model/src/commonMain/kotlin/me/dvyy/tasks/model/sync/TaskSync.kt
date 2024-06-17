package me.dvyy.tasks.model.sync

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Clock
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListKey
import me.dvyy.tasks.model.TaskModel

class TaskNetworkModel(
    val name: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val list: ListKey,
) {
    fun toTaskModel(uuid: Uuid): TaskModel {
        return TaskModel(
            uuid = uuid,
            name = name,
            completed = completed,
            highlight = highlight,
            modified = Clock.System.now()
        )
    }
}
