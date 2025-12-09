package me.dvyy.tasks

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.client.mutators.ActionQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.reducers.reducers
import me.dvyy.syncengine.server.schema.ServerActionProcessor
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.syncengine.server.schema.mockService
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.actions.JsonCreateAction
import me.dvyy.tasks.model.database.actions.JsonPatchAction
import me.dvyy.tasks.model.database.reducers.jsonReducers
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DbConnectionTest : DbTest() {
    val clientDatabase = Database.temporary()
    val serverDatabase = Database.temporary()

    @Test
    fun testDbConnection() = runTest {
        val reducers = reducers {
            jsonReducers(AppQueries(serverDatabase))
        }
        // Server
        val server = SyncServer(
            serverDatabase, AppSchema, ServerActionProcessor(reducers)
        )
        val mockSyncService = server.mockService(user = 0)

        // Client
        val clientActionQueue = ActionQueue(clientDatabase, reducers)
        val client = SyncClient(
            db = clientDatabase,
            actionQueue = clientActionQueue,
            schema = AppSchema,
            syncService = mockSyncService
        )

        client.initialize()
        server.initialize()

        val parent = Uuid.random()
        val json =
            Json.decodeFromString<JsonElement>("""{ "text":  "hello world", "parent":  "${parent.toHexString()}" }""")
        val json2 = Json.decodeFromString<JsonElement>("""{ "text":  "hello world 2" }""")

        val id = Uuid.random()
        clientActionQueue(JsonCreateAction(id = id, data = json))
        clientActionQueue(JsonPatchAction(id = id, patch = json2))
        client.sync()
        val serverTask = serverDatabase.read { AppQueries(serverDatabase).tasks.get(id) }
        val clientTask = serverDatabase.read { AppQueries(clientDatabase).tasks.get(id) }

        val expected = Task(text = "hello world 2", parent = parent)
        serverTask shouldBe expected
        clientTask shouldBe expected
    }
}
