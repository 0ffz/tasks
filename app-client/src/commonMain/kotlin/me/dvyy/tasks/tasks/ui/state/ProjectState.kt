package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import me.dvyy.tasks.model.TaskId

@Stable
data class ProjectState(
    val header: ProjectHeaderState,
    val children: List<TaskId>,
    val mutate: ProjectMutations,
)

interface ProjectMutations {
    fun addTask(atEnd: Boolean = true)
    fun moveTask(dragged: TaskId)
}