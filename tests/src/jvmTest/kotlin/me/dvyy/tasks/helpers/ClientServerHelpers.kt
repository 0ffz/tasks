package me.dvyy.tasks.helpers

import co.touchlab.kermit.Logger
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.server.schema.Workspace
import me.dvyy.syncengine.server.schema.mockService
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.commonSyncModule
import org.koin.dsl.koinApplication

class ClientServerHelpers(
    val clientPath: String,
    val serverPath: String,
) {
    val koin = koinApplication { modules(commonSyncModule()) }.koin
    val reducers = koin.get<Reducers>()
    val clientDb = Helpers.loadDbFromResource(clientPath)
    val serverDb = Helpers.loadDbFromResource(serverPath)
    val server = Workspace.of(serverDb, reducers, AppSchema)
    val mockSyncService = server.mockService(user = 0)

    // Client
    val syncClient = SyncClient.of(
        logger = Logger.withTag("Client"),
        db = clientDb,
        schema = AppSchema,
        reducers = reducers,
        syncService = mockSyncService,
    )
    val client = AppDatabase(clientDb, syncClient)
//    val server = Helpers.loadDbFromResource(serverPath)
}