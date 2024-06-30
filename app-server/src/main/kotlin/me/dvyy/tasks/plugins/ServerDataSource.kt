package me.dvyy.tasks.plugins

import kotlinx.datetime.Instant
import me.dvyy.tasks.db.Message
import me.dvyy.tasks.db.ServerDatabase
import me.dvyy.tasks.db.Task
import me.dvyy.tasks.db.TaskList
import me.dvyy.tasks.model.EntityType
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.*
import me.dvyy.tasks.model.network.NetworkMessage.Type.Delete
import me.dvyy.tasks.model.network.NetworkMessage.Type.Update

class ServerDataSource(
    private val database: ServerDatabase,
) {
//    object UserDAO : Table() {
//        val uuid = uuid("uuid").uniqueIndex()
//        val username = text("username").uniqueIndex()
//        override val primaryKey = PrimaryKey(uuid)
//    }
//
//    object MessageDAO : Table() {
//        val uuid = uuid("uuid").uniqueIndex()
//        val modified = timestamp("dateModified").index()
//
//        //        val created = timestamp("dateCreated")//.defaultExpression(modified)
//        val data = text("data")//, AppFormats.databaseJson, NetworkModel.serializer())
//        val user = reference("user", UserDAO.uuid).index()
//        override val primaryKey = PrimaryKey(uuid)
//    }
//
//    init {
//        transaction(database) {
//            SchemaUtils.create(MessageDAO, UserDAO)
//        }
//    }

    suspend fun resolveMessages(
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

        insertMessages(sendToServer, userSession)
        return Changelist(changelist.lastSynced, changelist.upTo, sendToClient)
    }

    private fun insertMessages(
        messages: List<NetworkMessage>,
        userSession: UserSession,
    ) = database.transaction {
        val user = userSession.userId
        messages.forEach { message ->
            val uuid = message.entityId
            database.messagesQueries.insertMessage(
                Message(
                    uuid = uuid,
                    modified = message.modified,
                    type = if (message.data is Deleted) Delete else Update,
                    userId = user,
                    entityType = message.data.entityType
                )
            )
            when (val data = message.data) {
                is Deleted -> {
                    when (data.entityType) {
                        EntityType.TASK -> database.tasksQueries.delete(TaskId(uuid))
                        EntityType.LIST -> database.listsQueries.delete(ListId(uuid))
                    }
                }

                is TaskListNetworkModel -> {
                    database.listsQueries.upsert(
                        TaskList(ListId(uuid), data.isProject, data.title, data.rank, user)
                    )
                }

                is TaskNetworkModel -> {
                    database.tasksQueries.upsert(
                        Task(TaskId(uuid), data.text, data.highlight, data.completed, data.listId, data.rank, user)
                    )
                }
            }
        }
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
                    data = TaskNetworkModel(it.list, it.text, it.completed, it.highlight, it.rank),
                    it.uuid, it.modified,
                )
            })

            addAll(database.messagesQueries.selectLists(user, lastSync, upTo).executeAsList().map {
                NetworkMessage(
                    data = TaskListNetworkModel(it.title, it.isProject, it.rank),
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
