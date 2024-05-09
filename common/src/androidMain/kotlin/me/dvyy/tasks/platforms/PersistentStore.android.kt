package me.dvyy.tasks.platforms

import kotlinx.datetime.LocalDate
import me.dvyy.tasks.serialization.Task

actual class PersistentStore {
    actual fun saveDay(date: LocalDate, tasks: List<Task>) {

    }

    actual fun loadTasksForDay(date: LocalDate): List<Task> {
        return listOf()
    }

}
