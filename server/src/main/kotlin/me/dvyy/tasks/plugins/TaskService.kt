package me.dvyy.tasks.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Task
import me.dvyy.tasks.plugins.TaskService.Tasks.completed
import me.dvyy.tasks.plugins.TaskService.Tasks.date
import me.dvyy.tasks.plugins.TaskService.Tasks.title
import me.dvyy.tasks.plugins.TaskService.Tasks.uuid
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TaskService(private val database: Database) {
    object Tasks : Table() {
        val uuid = uuid("uuid")
        val title = mediumText("title")
        val completed = bool("completed")
        val date = date("date").index()
        override val primaryKey = PrimaryKey(uuid)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun tasksForDate(date: LocalDate): List<Task> = dbQuery {
        Tasks.select { Tasks.date eq date }
            .map { Task(it[uuid], it[title], it[completed]) }
    }

    suspend fun update(forDate: LocalDate, tasks: List<Task>) {
        dbQuery {
            Tasks.deleteWhere { date eq forDate or uuid.inList(tasks.map { it.uuid }) }
            Tasks.batchInsert(tasks) { task ->
                this[uuid] = task.uuid
                this[title] = task.name
                this[completed] = task.completed
                this[date] = forDate
            }
        }
    }
}

