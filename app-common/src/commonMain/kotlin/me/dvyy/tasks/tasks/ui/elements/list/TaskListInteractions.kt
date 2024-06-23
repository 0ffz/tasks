package me.dvyy.tasks.tasks.ui.elements.list

import me.dvyy.tasks.model.TaskListProperties

data class TaskListInteractions(
    val createNewTask: () -> Unit = {},
    val onPropertiesChanged: (TaskListProperties) -> Unit = {},
)
