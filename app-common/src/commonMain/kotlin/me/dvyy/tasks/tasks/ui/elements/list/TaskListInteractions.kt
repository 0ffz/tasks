package me.dvyy.tasks.tasks.ui.elements.list

data class TaskListInteractions(
    val createNewTask: () -> Unit = {},
    val onTitleChange: (String) -> Unit = {},
)
