package me.dvyy.tasks.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskModel(
    val id: TaskId,
    val text: String = "",
    val completed: Boolean = true,
    val highlight: Highlight = Highlight.Unmarked,
)

