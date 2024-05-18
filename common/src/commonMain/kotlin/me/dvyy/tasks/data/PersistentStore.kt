package me.dvyy.tasks.data

import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.ui.elements.week.TaskListKey

expect class PersistentStore constructor() {
    fun saveList(key: TaskListKey, tasks: List<TaskModel>)

    fun loadTasksForList(key: TaskListKey): Result<List<TaskModel>>
}
