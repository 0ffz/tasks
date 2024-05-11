package me.dvyy.tasks.platforms

import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Task

expect class PersistentStore constructor() {
    fun saveDay(date: LocalDate, tasks: List<Task>)

    fun loadTasksForDay(date: LocalDate): List<Task>
}
