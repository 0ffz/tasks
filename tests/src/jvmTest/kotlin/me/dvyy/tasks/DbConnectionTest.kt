package me.dvyy.tasks

import me.dvyy.tasks.model.ListId.Companion.newProject
import me.dvyy.tasks.tasks.data.TasksLocalDataSource
import kotlin.test.Test

class DbConnectionTest : DbTest() {
    @Test
    fun testDbConnection() {
        val tasks = TasksLocalDataSource(clientDb)
        val task = tasks.createTask(newProject())
        tasks.getTask(task.uuid).let {
            assert(it == task)
        }
    }
}
