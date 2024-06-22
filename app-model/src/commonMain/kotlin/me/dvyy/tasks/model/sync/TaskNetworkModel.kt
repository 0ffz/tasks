package me.dvyy.tasks.model.sync

import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.ListId

@Serializable
class TaskNetworkModel(
    val name: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
    val list: ListId,
) {
}
