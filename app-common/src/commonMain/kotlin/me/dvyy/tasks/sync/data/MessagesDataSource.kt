package me.dvyy.tasks.sync.data

import app.cash.sqldelight.coroutines.asFlow
import com.benasher44.uuid.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.db.client.Database
import me.dvyy.tasks.db.client.Rank
import me.dvyy.tasks.db.client.Task
import me.dvyy.tasks.db.client.TaskList
import me.dvyy.tasks.model.EntityId
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.*

class MessagesDataSource(
    val db: Database,
) {
    /** Fills the message table with all entities, as if they were update [now] */
    suspend fun createMessagesForAllEntities(now: Instant) {
        db.transaction {
            db.tasksQueries.selectAllUUIDs().executeAsList().forEach {
                saveMessage(NetworkMessage.Type.Update, it, now)
            }

            db.listsQueries.selectAllUUIDs().executeAsList().forEach {
                saveMessage(NetworkMessage.Type.Update, it, now)
            }

            db.rankQueries.selectAllUUIDs().executeAsList().forEach {
                saveMessage(NetworkMessage.Type.Update, it, EntityType.RANK, now)
            }
        }
    }

    suspend fun getChanges(upTo: Instant): List<NetworkMessage> = db.transactionWithResult {
        buildList {
            addAll(db.messagesQueries.selectTasks(upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskNetworkModel(it.list, it.text, it.completed, it.highlight),
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
            addAll(db.messagesQueries.selectRanks(upTo).executeAsList().map {
                NetworkMessage(
                    data = RankNetworkModel(it.uuid, it.parent, it.rank),
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

    suspend fun applyMessages(messages: List<NetworkMessage>) = db.transaction {
        messages.forEach { message ->
            val uuid = message.entityId
            when (val data = message.data) {
                is Deleted -> when (data.entityType) {
                    EntityType.TASK -> db.tasksQueries.delete(TaskId(uuid))
                    EntityType.LIST -> db.listsQueries.delete(ListId(uuid))
                    EntityType.RANK -> db.rankQueries.delete(uuid)
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
                    )
                )

                is RankNetworkModel -> db.rankQueries.upsert(
                    Rank(
                        uuid = uuid,
                        parent = data.parent,
                        rank = data.rank,
                    )
                )
            }
        }
    }

    suspend fun clear(now: Instant) = db.messagesQueries.clear(now)

    suspend fun saveMessage(
        messageType: NetworkMessage.Type,
        uuid: EntityId,
        timestamp: Instant = Clock.System.now(),
    ) = db.messagesQueries.insert(
        uuid = uuid.uuid,
        modified = timestamp,
        type = messageType,
        entityType = uuid.type
    )

    suspend fun saveMessage(
        messageType: NetworkMessage.Type,
        uuid: Uuid,
        entityType: EntityType,
        timestamp: Instant = Clock.System.now(),
    ) = db.messagesQueries.insert(
        uuid = uuid,
        modified = timestamp,
        type = messageType,
        entityType = entityType
    )

    fun observeLastUpdated() = db.messagesQueries.lastUpdate().asFlow()
}
