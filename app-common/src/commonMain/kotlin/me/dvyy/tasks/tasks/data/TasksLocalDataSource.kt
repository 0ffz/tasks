package me.dvyy.tasks.tasks.data

import me.dvyy.tasks.model.ListKey

expect class TasksLocalDataSource constructor() {
    fun saveList(key: ListKey, list: TaskListModel)

    fun loadTasksForList(key: ListKey): Result<TaskListModel?>

    fun getProjects(): List<ListKey.Project>

    fun deleteList(key: ListKey)
}
