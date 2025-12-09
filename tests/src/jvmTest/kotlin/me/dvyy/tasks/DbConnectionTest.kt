package me.dvyy.tasks

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
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.actions.JsonCreateAction
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
            jsonReducers(AppDAO(serverDatabase))
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
            mutators = clientActionQueue,
            schema = AppSchema,
            syncService = mockSyncService
        )

        client.initialize()
        server.initialize()

        val json = Json.decodeFromString<JsonElement>("""{ "text":  "hello world" }""")

        clientActionQueue(JsonCreateAction(id = Uuid.random(), data = json))
        client.sync()
    }
}
