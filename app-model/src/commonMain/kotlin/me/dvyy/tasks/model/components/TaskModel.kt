package me.dvyy.tasks.model.components

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.Serializable
import me.dvyy.tasks.model.Highlight

@Serializable
data class TaskModel(
    val text: String,
    @EncodeDefault(Mode.ALWAYS)
    val done: Boolean = false,
    val highlight: Highlight? = null,
//    val parent: Uuid? = null,
//    val rank: String? = null,
)
