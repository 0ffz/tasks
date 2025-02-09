package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import me.dvyy.tasks.model.Highlight
import org.dizitart.kno2.documentOf

@Stable
data class TaskUiState(
    val text: String,
    val completed: Boolean,
    val highlight: Highlight,
) {
    fun toFrontMatter() = documentOf(
        "done" to completed,
//        "highlight" to highlight,
    )
    companion object {
//        fun fromModel(model: Task) = TaskUiState(
//            text = model.text ?: "",
//            completed = model.completed,
//            highlight = model.highlight,
//        )
    }
}
