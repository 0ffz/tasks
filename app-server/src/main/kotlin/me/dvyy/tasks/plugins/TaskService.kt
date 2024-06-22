package me.dvyy.tasks.plugins

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.model.Changelist
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.Message
import me.dvyy.tasks.model.sync.TaskListNetworkModel
import me.dvyy.tasks.model.sync.TaskNetworkModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.statements.BaseBatchInsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class TaskService(private val database: Database) {
    object Projects : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val name = varchar("name", 255)
        override val primaryKey = PrimaryKey(uuid)
    }

    object SyncTimestamps : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val timestamp = timestamp("dateModified").index()
        val created = timestamp("dateCreated")
        val deleted = bool("deleted")
        override val primaryKey = PrimaryKey(uuid)
    }

    object Tasks : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val title = mediumText("title")
        val completed = bool("completed")
        val highlight = enumeration<Highlight>("highlight")
        val date = date("date").nullable().index()
        val project = uuid("project").nullable()
        override val primaryKey = PrimaryKey(uuid)

        fun getListKey(row: ResultRow): Uuid {
            val date = row[date]
            if (date != null) return Uuid.Date(date)
            val project = row[project]
            if (project != null) return Uuid.Project(project)
            error("Task not in any list")
        }

        fun setListKey(row: BaseBatchInsertStatement, key: Uuid) = when (key) {
            is Uuid.Date -> row[date] = key.date
            is Uuid.Project -> row[project] = key.uuid
        }
    }

    init {
        transaction(database) {
            SchemaUtils.create(Tasks, SyncTimestamps, Projects)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // Timestamped messages helpers

    suspend fun <T> applyMessages(
        changelist: Changelist<T>,
        upsert: (List<Message.Update<T>>) -> Unit,
        delete: (List<Message.Delete<T>>) -> Unit,
    ) {
        dbQuery {
            val compacted = changelist.compactedMap().toMutableMap()
            SyncTimestamps
                .select(SyncTimestamps.uuid, SyncTimestamps.timestamp)
                .where {
                    (SyncTimestamps.uuid inList compacted.keys).run {
                        if (changelist.lastSynced != null) and(SyncTimestamps.timestamp greaterEq changelist.lastSynced!!)
                        else this
                    }
                }.forEach {
                    val uuid = it[SyncTimestamps.uuid]
                    val dbTimestamp = it[SyncTimestamps.timestamp]
                    val messageTimestamp = compacted[uuid]?.timestamp ?: return@forEach

                    // Resolve conflicts by last write wins
                    if (dbTimestamp > messageTimestamp)
                        compacted.remove(uuid)
                }
            SyncTimestamps.batchUpsert(compacted.values) { message ->
                this[SyncTimestamps.uuid] = message.uuid
                this[SyncTimestamps.timestamp] = message.timestamp
                this[SyncTimestamps.deleted] = message is Message.Delete
//                this[SyncTimestamps.created] = it.timestamp TODO
            }
            delete(compacted.values.filterIsInstance<Message.Delete<T>>())
            upsert(compacted.values.filterIsInstance<Message.Update<T>>())
        }
    }

    suspend fun <T> getMessages(
        lastSync: Instant?,
        upTo: Instant,
        filterQuery: (SqlExpressionBuilder.() -> Expression<Boolean>)? = null,
        joinBy: Column<UUID>,
        map: (ResultRow) -> T
    ): Changelist<T> = dbQuery {
        val messages = SyncTimestamps.join(
            joinBy.table,
            JoinType.INNER,
            onColumn = SyncTimestamps.uuid,
            otherColumn = joinBy,
        )
            .selectAll()
            .where {
                SyncTimestamps.timestamp.between(lastSync, upTo).run {
                    if (filterQuery != null) and(filterQuery())
                    else this
                }
            }
            .map {
                val uuid = it[SyncTimestamps.uuid]
                val timestamp = it[SyncTimestamps.timestamp]
                if (it[SyncTimestamps.deleted]) Message.Delete(uuid, timestamp)
                else Message.Update(map(it), uuid, timestamp)
            }

        Changelist(lastSync, messages)
    }

    private fun ResultRow.toTask() = TaskNetworkModel(
        this[Tasks.title],
        this[Tasks.completed],
        this[Tasks.highlight],
        Tasks.getListKey(this),
    )

    // Tasks messages

    suspend fun getTaskMessages(date: LocalDate, since: Instant): Changelist<TaskNetworkModel> = getMessages(
        since,
        since, //TODO now
        filterQuery = { Tasks.date eq date },
        joinBy = Tasks.uuid,
        map = { it.toTask() }
    )

    suspend fun applyTaskMessages(changelist: Changelist<TaskNetworkModel>) = applyMessages(
        changelist,
        upsert = {
            Tasks.batchUpsert(it) { (model, uuid) ->
                this[Tasks.uuid] = uuid
                this[Tasks.title] = model.name
                this[Tasks.completed] = model.completed
                this[Tasks.highlight] = model.highlight
                Tasks.setListKey(this, model.list)
            }
        },
        delete = { tasks ->
            Tasks.deleteWhere { uuid inList tasks.map { it.uuid } }
        }
    )

    // Project messages

    suspend fun getProjectMesages(since: Instant?): Changelist<TaskListNetworkModel> = getMessages(
        since,
        TODO(), //now
        joinBy = Projects.uuid,
        map = {
            TaskListNetworkModel(
                Uuid.Project(it[Projects.uuid]),
                it[Projects.name]
            )
        },
    )

    suspend fun applyProjectMessages(changelist: Changelist<TaskListNetworkModel>) = applyMessages(
        changelist,
        upsert = {
            Projects.batchUpsert(it) { (model, uuid) ->
                this[Projects.uuid] = uuid
                this[Projects.name] = model.title
            }
        },
        delete = { projects ->
            Projects.deleteWhere { uuid inList projects.map { it.uuid } }
        }
    )
//
//    suspend fun update(changelist: TaskChangeList) = dbQuery {
//        changelist.apply {
//            Tasks.deleteWhere { uuid inList deletedTasks.map { it.data } }
//            updatedTasks.forEach { (list, tasks) ->
//                Tasks.batchReplace(tasks) { task ->
//                    when (list) {
//                        is ListKey.Date -> this[date] = list.date
//                        is ListKey.Project -> this[project] = list.uuid
//                    }
//                }
//            }
//        }
//    }
//
//    suspend fun update(forDate: LocalDate, tasks: List<TaskModel>) {
//        dbQuery {
//            // Delete any tasks that are no longer present for this date
//            Tasks.deleteWhere { date eq forDate and uuid.notInList(tasks.map { it.uuid }) }
//
//            // Insert or replace any new tasks
//            Tasks.batchReplace(tasks) { task ->
//                this[uuid] = task.uuid
//                this[title] = task.name
//                this[completed] = task.completed
//                this[highlight] = task.highlight
//                this[date] = forDate
//            }
//        }
//    }

//    suspend fun tasksForList(key: ListKey): List<TaskModel> = dbQuery {
//        when (key) {
//            is ListKey.Project -> {
//                Tasks.select { Tasks.project eq key.uuid }.map { it.toModel() }
//            }
//
//            is ListKey.Date -> {
//                Tasks.select { date eq key.date }.map { it.toModel() }
//            }
//        }
//    }
}

