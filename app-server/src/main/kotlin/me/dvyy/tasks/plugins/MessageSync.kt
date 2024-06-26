package me.dvyy.tasks.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.network.Changelist
import me.dvyy.tasks.model.network.NetworkMessage
import me.dvyy.tasks.model.network.NetworkModel
import me.dvyy.tasks.model.serializers.AppFormats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class MessageSync(
    private val database: Database,
) {
    object MessageDAO : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val modified = timestamp("dateModified").index()

        //        val created = timestamp("dateCreated")//.defaultExpression(modified)
        val data = text("data")//, AppFormats.databaseJson, NetworkModel.serializer())
        override val primaryKey = PrimaryKey(uuid)
    }

    init {
        transaction(database) {
            SchemaUtils.create(MessageDAO)
        }
    }

    suspend fun resolveMessages(changelist: Changelist): Changelist = dbQuery {
        val serverMessages = getMessages(changelist.lastSynced, changelist.upTo)
        val clientUpdates = changelist.messages.associateByTo(mutableMapOf()) { it.entityId }
        val serverUpdates = serverMessages.associateByTo(mutableMapOf()) { it.entityId }

        val sendToClient = serverMessages
            .filter { it.modified > (clientUpdates[it.entityId]?.modified ?: return@filter true) }

        val sendToServer = changelist.messages
            .filter { it.modified > (serverUpdates[it.entityId]?.modified ?: return@filter true) }

        insertMessages(sendToServer)
        Changelist(changelist.lastSynced, changelist.upTo, sendToClient)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    private suspend fun insertMessages(message: List<NetworkMessage>) = dbQuery {
        MessageDAO.batchUpsert(message) { msg ->
            this[MessageDAO.uuid] = msg.entityId
            this[MessageDAO.modified] = msg.modified
            this[MessageDAO.data] = AppFormats.databaseJson.encodeToString(NetworkModel.serializer(), msg.data)
        }
    }

    private suspend fun getMessages(
        lastSync: Instant,
        upTo: Instant,
    ): List<NetworkMessage> = dbQuery {
        MessageDAO.selectAll().where {
            (MessageDAO.modified greaterEq lastSync) and (MessageDAO.modified lessEq upTo)
        }.map {
            NetworkMessage(
                AppFormats.json.decodeFromString(NetworkModel.serializer(), it[MessageDAO.data]),
                it[MessageDAO.uuid],
                it[MessageDAO.modified],
            )
        }
    }
}
