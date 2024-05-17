package me.dvyy.tasks.data

import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.TaskModel

expect class PersistentStore constructor() {
    fun saveDay(date: LocalDate, tasks: List<TaskModel>)

    fun loadTasksForDay(date: LocalDate): Result<List<TaskModel>>
}
