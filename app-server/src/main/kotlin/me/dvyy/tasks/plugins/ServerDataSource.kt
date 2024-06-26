package me.dvyy.tasks.plugins

import com.benasher44.uuid.Uuid
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
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

class ServerDataSource(
    private val database: Database,
) {
    object UserDAO : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val username = text("username").uniqueIndex()
        val digest = binary("digest")
        override val primaryKey = PrimaryKey(uuid)
    }

    object MessageDAO : Table() {
        val uuid = uuid("uuid").uniqueIndex()
        val modified = timestamp("dateModified").index()

        //        val created = timestamp("dateCreated")//.defaultExpression(modified)
        val data = text("data")//, AppFormats.databaseJson, NetworkModel.serializer())
        val user = reference("user", UserDAO.uuid).index()
        override val primaryKey = PrimaryKey(uuid)
    }

    init {
        transaction(database) {
            SchemaUtils.create(MessageDAO, UserDAO)
        }
    }

    suspend fun resolveMessages(
        changelist: Changelist,
        userSession: UserSession,
    ): Changelist = dbQuery {
        val serverMessages = getMessages(changelist.lastSynced, changelist.upTo, userSession)
        val clientUpdates = changelist.messages.associateByTo(mutableMapOf()) { it.entityId }
        val serverUpdates = serverMessages.associateByTo(mutableMapOf()) { it.entityId }

        val sendToClient = serverMessages
            .filter { it.modified > (clientUpdates[it.entityId]?.modified ?: return@filter true) }

        val sendToServer = changelist.messages
            .filter { it.modified > (serverUpdates[it.entityId]?.modified ?: return@filter true) }

        insertMessages(sendToServer, userSession)
        Changelist(changelist.lastSynced, changelist.upTo, sendToClient)
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    private suspend fun insertMessages(
        message: List<NetworkMessage>,
        userSession: UserSession,
    ) = dbQuery {
        MessageDAO.batchUpsert(message) { msg ->
            this[MessageDAO.uuid] = msg.entityId
            this[MessageDAO.modified] = msg.modified
            this[MessageDAO.data] = AppFormats.databaseJson.encodeToString(NetworkModel.serializer(), msg.data)
            this[MessageDAO.user] = userSession.uuid
        }
    }

    private suspend fun getMessages(
        lastSync: Instant,
        upTo: Instant,
        userSession: UserSession,
    ): List<NetworkMessage> = dbQuery {
        MessageDAO.selectAll()
            .where { (MessageDAO.user eq userSession.uuid) and (MessageDAO.modified greaterEq lastSync) and (MessageDAO.modified lessEq upTo) }
            .map {
                NetworkMessage(
                    AppFormats.json.decodeFromString(NetworkModel.serializer(), it[MessageDAO.data]),
                    it[MessageDAO.uuid],
                    it[MessageDAO.modified],
                )
            }
    }

//    suspend fun createUser(request: UserCreationRequest): Unit = dbQuery {
//        val hashedPassword = getMd5Digest("${request.username}:${Realms.user}:${request.digest}")
//        UserDAO.insert {
//            it[UserDAO.uuid] = uuid4()
//            it[UserDAO.username] = request.username
//            it[UserDAO.digest] = hashedPassword
//        }
//    }

    suspend fun getUserDigest(username: String): ByteArray? = dbQuery {
        UserDAO.select(UserDAO.digest).where { UserDAO.username eq username }.singleOrNull()?.get(UserDAO.digest)
    }

    suspend fun getOrCreateUserUuid(username: String): Uuid = dbQuery {
        UserDAO.select(UserDAO.uuid)
            .where { UserDAO.username eq username }
            .singleOrNull()
            ?.get(UserDAO.uuid)
            ?: UserDAO.insert {
                it[UserDAO.uuid] = Uuid.randomUUID()
                it[UserDAO.username] = username

            }[UserDAO.uuid]
    }

    fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

}

object Realms {
    val user = "Access to the '/' path"
    val userCreate = "Create a new user"
}
