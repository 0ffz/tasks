package me.dvyy.tasks.sync.data

import app.cash.sqldelight.coroutines.asFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.Deleted
import me.dvyy.tasks.model.network.NetworkMessage
import me.dvyy.tasks.model.network.TaskListNetworkModel
import me.dvyy.tasks.model.network.TaskNetworkModel

class MessagesDataSource(
    val db: Database,
) {
    /** Fills the message table with all entities, as if they were update [now] */
    fun createMessagesForAllEntities(now: Instant) {
        db.transaction {
            db.tasksQueries.selectAllUUIDs().executeAsList().forEach {
                saveMessage(NetworkMessage.Type.Update, it, now)
            }

            db.listsQueries.selectAllUUIDs().executeAsList().forEach {
                saveMessage(NetworkMessage.Type.Update, it, now)
            }
        }
    }

    fun getChanges(upTo: Instant): List<NetworkMessage> = db.transactionWithResult {
        buildList {
            addAll(db.messagesQueries.selectTasks(upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskNetworkModel(it.list, it.text, it.completed, it.highlight, it.rank),
                    entityId = it.uuid,
                    modified = it.modified,
                )
            })
            addAll(db.messagesQueries.selectLists(upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskListNetworkModel(it.title, it.isProject, it.rank),
                    entityId = it.uuid,
                    modified = it.modified,
                )
            })
            addAll(db.messagesQueries.selectDeleted(upTo).executeAsList().map {
                NetworkMessage(
                    data = Deleted(it.entityType),
                    entityId = it.uuid,
                    modified = it.modified,
                )
            })
        }
    }

    fun applyMessages(messages: List<NetworkMessage>) = db.transaction {
        messages.forEach { message ->
            val uuid = message.entityId
            when (val data = message.data) {
                is Deleted -> when (data.entityType) {
                    EntityType.TASK -> db.tasksQueries.delete(TaskId(uuid))
                    EntityType.LIST -> db.listsQueries.delete(ListId(uuid))
                }

                is TaskListNetworkModel -> db.listsQueries.insert(
                    TaskList(
                        uuid = ListId(uuid),
                        title = data.title,
                        isProject = data.isProject,
                        rank = data.rank,
                    )
                )

                is TaskNetworkModel -> db.tasksQueries.upsert(
                    Task(
                        uuid = TaskId(uuid),
                        text = data.text,
                        highlight = data.highlight,
                        completed = data.completed,
                        list = data.listId,
                        rank = data.rank,
                    )
                )
            }
        }
    }

    fun clear(now: Instant) = db.messagesQueries.clear(now)

    fun saveMessage(
        messageType: NetworkMessage.Type,
        uuid: EntityId,
        timestamp: Instant = Clock.System.now(),
    ) = db.messagesQueries.insert(uuid.uuid, timestamp, messageType, uuid.type)

    fun observeLastUpdated() = db.messagesQueries.lastUpdate().asFlow()
}
