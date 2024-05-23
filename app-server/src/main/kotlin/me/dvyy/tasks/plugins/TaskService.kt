package me.dvyy.tasks.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.*
import me.dvyy.tasks.plugins.TaskService.Tasks.completed
import me.dvyy.tasks.plugins.TaskService.Tasks.date
import me.dvyy.tasks.plugins.TaskService.Tasks.highlight
import me.dvyy.tasks.plugins.TaskService.Tasks.lastModified
import me.dvyy.tasks.plugins.TaskService.Tasks.project
import me.dvyy.tasks.plugins.TaskService.Tasks.title
import me.dvyy.tasks.plugins.TaskService.Tasks.uuid
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TaskService(private val database: Database) {
    object Projects : Table() {
        val id = uuid("uuid")
        val name = varchar("name", 255)
        override val primaryKey = PrimaryKey(id)
    }

    object DeletedTasks : Table() {
        val uuid = uuid("uuid")
        val dateDeleted = timestamp("date")
        val dateCreated = timestamp("dateCreated")
        override val primaryKey = PrimaryKey(uuid)
    }

    object Tasks : Table() {
        val uuid = uuid("uuid")
        val title = mediumText("title")
        val completed = bool("completed")
        val highlight = enumeration<Highlight>("highlight")
        val dateCreated = date("dateCreated")
        val date = date("date").index()
        val project = uuid("project")
        val lastModified = timestamp("lastModified").index()
        override val primaryKey = PrimaryKey(uuid)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks, DeletedTasks, Projects)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun tasksForList(key: ListKey): List<TaskModel> = dbQuery {
        when (key) {
            is ListKey.Project -> {
                Tasks.select { Tasks.project eq key.uuid }.map { it.toModel() }
            }

            is ListKey.Date -> {
                Tasks.select { date eq key.date }.map { it.toModel() }
            }
        }
    }

    private fun ResultRow.toModel() = TaskModel(
        this[uuid],
        this[title],
        this[completed],
        this[highlight],
        this[lastModified]
    )

    suspend fun getChangelistFor(lists: List<ListKey>, lastSync: Instant?, upTo: Instant): TaskChangeList {
        return dbQuery {
            TaskChangeList(
                updatedTasks = lists.map { list ->
                    list to Tasks
                        .select { lastModified.between(lastSync, upTo) }
                        .map { it.toModel() }
                }.toMap(),
                deletedTasks = DeletedTasks
                    .run {
                        if (lastSync == null) selectAll()
                        else select { (dateDeleted greaterEq lastSync) and (dateCreated lessEq lastSync) }
                    }
                    .map { Timestamped(it[DeletedTasks.uuid], it[DeletedTasks.dateDeleted]) },
            )
        }
    }

    suspend fun update(changelist: TaskChangeList) = dbQuery {
        changelist.apply {
            Tasks.deleteWhere { uuid inList deletedTasks.map { it.data } }
            updatedTasks.forEach { (list, tasks) ->
                Tasks.batchReplace(tasks) { task ->
                    this[uuid] = task.uuid
                    this[title] = task.name
                    this[completed] = task.completed
                    this[highlight] = task.highlight
                    when (list) {
                        is ListKey.Date -> this[date] = list.date
                        is ListKey.Project -> this[project] = list.uuid
                    }
                }
            }
        }
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

