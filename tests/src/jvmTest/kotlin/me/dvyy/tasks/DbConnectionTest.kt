package me.dvyy.tasks

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.MutatorApplier
import me.dvyy.syncengine.SyncServer
import me.dvyy.syncengine.client.mutators.MutatorQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.mockService
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.mutators.JsonCreateMutator
import me.dvyy.tasks.model.database.mutators.Mutator
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DbConnectionTest : DbTest() {
    val clientDatabase = Database.temporary()
    val serverDatabase = Database.temporary()

    @Test
    fun testDbConnection() = runTest {
        // Server
        val server = SyncServer(
            serverDatabase, AppSchema, MutatorApplier(
                AppDAO(serverDatabase),
                Mutator.serializer()
            )
        )
        val mockSyncService = server.mockService(user = 0)

        // Client
        val clientMutatorQueue = MutatorQueue(clientDatabase, AppDAO(clientDatabase), Mutator.serializer())
        val client = SyncClient(
            db = clientDatabase,
            mutators = clientMutatorQueue,
            schema = AppSchema,
            syncService = mockSyncService
        )

        client.initialize()
        server.initialize()

        val json = Json.decodeFromString<JsonElement>("""{ "text":  "hello world" }""")

        clientMutatorQueue(JsonCreateMutator(id = Uuid.random(), data = json))
        client.sync()
    }
}
