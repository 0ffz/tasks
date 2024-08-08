package me.dvyy.tasks.plugins

import kotlinx.datetime.Instant
import me.dvyy.tasks.db.migrations.Message
import me.dvyy.tasks.db.migrations.Rank
import me.dvyy.tasks.db.migrations.Task
import me.dvyy.tasks.db.migrations.TaskList
import me.dvyy.tasks.db.server.ServerDatabase
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.database.RankFunctions
import me.dvyy.tasks.model.network.*
import me.dvyy.tasks.model.network.NetworkMessage.Type.Delete
import me.dvyy.tasks.model.network.NetworkMessage.Type.Update
import java.util.*

class ServerDataSource(
    private val database: ServerDatabase,
) {
    fun resolveMessages(
        changelist: Changelist,
        userSession: UserSession,
    ): Changelist {
        val serverMessages = getMessages(changelist.lastSynced ?: Instant.DISTANT_PAST, changelist.upTo, userSession)
        val clientUpdates = changelist.messages.associateByTo(mutableMapOf()) { it.entityId }
        val serverUpdates = serverMessages.associateByTo(mutableMapOf()) { it.entityId }

        val sendToClient = serverMessages
            .filter { it.modified > (clientUpdates[it.entityId]?.modified ?: return@filter true) }

        val sendToServer = changelist.messages
            .filter { it.modified > (serverUpdates[it.entityId]?.modified ?: return@filter true) }

        //TODO correctly merge new messages with old ones when there are duplicates (avoids sending extra data)
        val resolvedConflictMessages = insertMessages(changelist.upTo, sendToServer, userSession)
        return Changelist(changelist.lastSynced, changelist.upTo, sendToClient + resolvedConflictMessages)
    }

    private fun insertMessages(
        now: Instant,
        messages: List<NetworkMessage>,
        userSession: UserSession,
    ) = database.transactionWithResult {
        val newMessages = mutableListOf<NetworkMessage>()
        val user = userSession.userId
        messages.forEach { message ->
            val uuid = message.entityId
            database.messagesQueries.insertMessage(
                Message(
                    uuid = uuid,
                    modified = message.modified,
                    inserted = now,
                    type = if (message.data is Deleted) Delete else Update,
                    userId = user,
                    entityType = message.data.entityType
                )
            )
            when (val data = message.data) {
                is Deleted -> {
                    when (data.entityType) {
                        EntityType.TASK -> database.tasksQueries.delete(user, TaskId(uuid))
                        EntityType.LIST -> database.listsQueries.delete(user, ListId(uuid))
                        EntityType.RANK -> database.rankQueries.delete(user, uuid)
                    }
                }

                is TaskListNetworkModel -> {
                    database.listsQueries.upsert(
                        TaskList(ListId(uuid), data.isProject, data.title, data.rank, user)
                    )
                }

                is TaskNetworkModel -> {
                    database.tasksQueries.upsert(
                        Task(TaskId(uuid), data.text, data.highlight, data.completed, data.listId, user)
                    )
                }

                is RankNetworkModel -> {
                    val existing = database.rankQueries.get(user, data.parent, data.rank).executeAsOneOrNull()

                    @Suppress("KotlinConstantConditions") // Kotlin compiler doesn't realize Uuid is a typealias for UUID on jvm because network model comes from multiplatform
                    if (existing?.rank == data.rank && existing.uuid == (data.uuid as UUID))
                        return@forEach

                    if (existing != null) {
                        val nextRank = database.rankQueries.nextItem(user, data.parent, data.rank)
                            .executeAsOneOrNull()
                            ?.rank
                            ?: RankFunctions.lastChar.toString()
                        val between = RankFunctions.getLexicographicMiddle(data.rank, nextRank)
                        newMessages.add(NetworkMessage(data.copy(rank = between), uuid, message.modified))
                        database.rankQueries.upsert(Rank(data.uuid, data.parent, between, user))
                    } else {
                        database.rankQueries.upsert(Rank(data.uuid, data.parent, data.rank, user))
                    }
                }
            }
        }
        return@transactionWithResult newMessages
    }

    private fun getMessages(
        lastSync: Instant,
        upTo: Instant,
        userSession: UserSession,
    ): List<NetworkMessage> = database.transactionWithResult {
        val user = userSession.userId
        buildList {
            addAll(database.messagesQueries.selectTasks(user, lastSync, upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskNetworkModel(it.list, it.text, it.completed, it.highlight),
                    it.uuid, it.modified,
                )
            })

            addAll(database.messagesQueries.selectLists(user, lastSync, upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskListNetworkModel(it.title, it.isProject, it.rank),
                    it.uuid, it.modified,
                )
            })

            addAll(database.messagesQueries.selectRanks(user, lastSync, upTo).executeAsList().map {
                NetworkMessage(
                    data = RankNetworkModel(it.uuid, it.parent, it.rank),
                    it.uuid, it.modified,
                )
            })

            addAll(database.messagesQueries.selectDeleted(user, lastSync, upTo).executeAsList().map {
                NetworkMessage(data = Deleted(it.entityType), it.uuid, it.modified)
            })
        }
    }

    fun getOrCreateUserId(username: String): Int = database.transactionWithResult {
        database.usersQueries.selectIdByName(username)
            .executeAsOneOrNull()
            ?: run {
                database.usersQueries.insertUser(username).executeAsOne()
            }
    }

}
