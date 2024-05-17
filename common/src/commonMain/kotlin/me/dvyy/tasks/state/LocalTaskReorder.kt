package me.dvyy.tasks.state

import androidx.compose.runtime.compositionLocalOf
import me.dvyy.tasks.stateholder.TaskReorderInteractions

val LocalTaskReorder = compositionLocalOf<TaskReorderInteractions> { error("No TaskReorder provided") }
