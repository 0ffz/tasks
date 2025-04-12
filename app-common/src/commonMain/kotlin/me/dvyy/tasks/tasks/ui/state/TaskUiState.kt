package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.tasks.data.TaskModel.done

@Stable
data class TaskUiState(
    val text: String,
    val completed: Boolean,
    val highlight: Color = Color.Transparent,
) {
    fun toFrontMatter() = NoteFrontMatter.new {
        done = completed
    }
}
