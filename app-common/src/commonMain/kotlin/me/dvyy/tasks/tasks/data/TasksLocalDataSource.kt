package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.tasks.ui.elements.list.TaskListKey

expect class TasksLocalDataSource constructor() {
    fun saveList(key: TaskListKey, tasks: List<TaskModel>)

    fun loadTasksForList(key: TaskListKey): Result<List<TaskModel>>

    fun getProjects(): Result<List<TaskListKey.Project>>
}
