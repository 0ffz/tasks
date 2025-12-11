package me.dvyy.tasks

import androidx.compose.ui.window.application
import co.touchlab.kermit.Logger
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.client.mutators.ActionQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.jsonactions.actions.JsonCreateAction
import me.dvyy.syncengine.jsonactions.actions.JsonPatchAction
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.server.schema.ServerActionProcessor
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.syncengine.server.schema.mockService
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.app.createAppKoinApplication
import me.dvyy.tasks.app.ui.AppDesktop
import me.dvyy.tasks.helpers.loggerNamed
import me.dvyy.tasks.model.components.Task
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.AppQueries
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.commonSyncModule
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DbConnectionTest : DbTest() {
    val clientDatabase = Database.temporary()
    val serverDatabase = Database.temporary()
    val koin = koinApplication {
        modules(commonSyncModule())
    }.koin
    val reducers = koin.get<Reducers>()

    @Test
    fun testDbConnection() = runTest {
        // Server
        val server = SyncServer(
            Logger, serverDatabase, AppSchema, ServerActionProcessor(Logger, reducers)
        )
        val mockSyncService = server.mockService(user = 0)

        // Client
        val clientActionQueue = ActionQueue(Logger, clientDatabase, reducers)
        val client = SyncClient(
            logger = Logger,
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
        val id2 = Uuid.random()
        clientActionQueue(JsonCreateAction(table = "notes", id = id, data = json))
        clientActionQueue(JsonCreateAction(table = "notes", id = id2, data = json))
        clientActionQueue(JsonPatchAction(table = "notes", id = id, patch = json2))
        client.sync()
        val serverTask = serverDatabase.read {
            val queries = AppQueries().tasks
            queries.get(id) to queries.get(id2)
        }
        val clientTask = serverDatabase.read {
            val queries = AppQueries().tasks
            queries.get(id) to queries.get(id2)
        }

        val expected = Task(text = "hello world 2", parent = parent) to Task(text = "hello world", parent = parent)
        serverTask shouldBe expected
        clientTask shouldBe expected
    }

    @Test
    fun rollbackTest() = runTest {
        val serverLogger = loggerNamed("Server")
        val server = SyncServer(
            serverLogger,
            serverDatabase, AppSchema, ServerActionProcessor(serverLogger, reducers)
        )
        server.initialize()
        val application = createAppKoinApplication(module {
            single<Database> { Database.temporary() }
            single<SyncService> { server.mockService(user = 0, 0.5.seconds) }
        })
        val listId = Uuid.random()
        application.koin.get<AppDatabase>().mutate.tasks.create(Task(text = "1", parent = listId))
        application.koin.get<AppDatabase>().mutate.tasks.create(Task(text = "2", parent = listId))
        application.koin.get<SyncClient>().sync()
    }

    @Test
    fun applicationTest() = runTest {
        val serverLogger = loggerNamed("Server")
        val server = SyncServer(
            serverLogger,
            serverDatabase, AppSchema, ServerActionProcessor(serverLogger, reducers)
        )
        server.initialize()
        application {
            AppDesktop(overrides = module {
                single { loggerNamed("Client 1") }
                single<Database> { Database.temporary() }
                single<SyncService> { server.mockService(user = 0, 0.2.seconds) }
            })
            AppDesktop(overrides = module {
                single { loggerNamed("Client 2") }
                single<Database> { Database.temporary() }
                single<SyncService> { server.mockService(user = 0, 0.8.seconds) }
            })
        }
    }
}
