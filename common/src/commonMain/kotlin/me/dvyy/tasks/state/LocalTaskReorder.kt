package me.dvyy.tasks.state

import androidx.compose.runtime.compositionLocalOf
import me.dvyy.tasks.stateholder.TaskReorder

val LocalTaskReorder = compositionLocalOf<TaskReorder> { error("No TaskReorder provided") }
