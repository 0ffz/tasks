package me.dvyy.tasks.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.TaskModel
import me.dvyy.tasks.plugins.TaskService.Tasks.completed
import me.dvyy.tasks.plugins.TaskService.Tasks.date
import me.dvyy.tasks.plugins.TaskService.Tasks.highlight
import me.dvyy.tasks.plugins.TaskService.Tasks.title
import me.dvyy.tasks.plugins.TaskService.Tasks.uuid
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TaskService(private val database: Database) {
    object Tasks : Table() {
        val uuid = uuid("uuid")
        val title = mediumText("title")
        val completed = bool("completed")
        val highlight = enumeration<Highlight>("highlight")
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

    suspend fun tasksForDate(date: LocalDate): List<TaskModel> = dbQuery {
        Tasks.select { Tasks.date eq date }
            .map { TaskModel(it[uuid], it[title], it[completed], it[highlight]) }
    }

    suspend fun update(forDate: LocalDate, tasks: List<TaskModel>) {
        dbQuery {
            // Delete any tasks that are no longer present for this date
            Tasks.deleteWhere { date eq forDate and uuid.notInList(tasks.map { it.uuid }) }

            // Insert or replace any new tasks
            Tasks.batchReplace(tasks) { task ->
                this[uuid] = task.uuid
                this[title] = task.name
                this[completed] = task.completed
                this[highlight] = task.highlight
                this[date] = forDate
            }
        }
    }
}

