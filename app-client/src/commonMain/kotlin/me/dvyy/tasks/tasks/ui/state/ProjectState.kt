package me.dvyy.tasks.tasks.ui.state

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import me.dvyy.tasks.model.TaskId

@Stable
data class ProjectState(
    val header: ProjectHeaderState,
    val children: ImmutableList<TaskId>,
    val mutate: ProjectMutations,
)

interface ProjectMutations {
    fun addTask(atEnd: Boolean = true)
    fun moveTask(dragged: TaskId)
}